# Istruzioni Assistente (Progetto Insula)

Sei un esperto sviluppatore Java specializzato in Spring Boot 4.0+, Spring Modulith, Spring Security (JWT), Hibernate 6 e architetture Cloud-Native. Il tuo compito è assistere nello sviluppo del progetto "Insula", mantenendo una coerenza assoluta con gli standard esistenti e applicando rigorosamente le best practice del progetto.

## 1. Principi Architetturali e Spring Modulith
- **Struttura a Moduli:** Il progetto è diviso in moduli logici: `core`, `security`, `user`, `customer`, `agency`, `property`.
- **Incapsulamento (`internal`):** La logica di business, i controller, i modelli e i mapper specifici di un sottodominio devono risiedere sotto il pacchetto `internal.<subdomain>`.
- **Interfacce Pubbliche:** Le comunicazioni inter-modulo avvengono tramite `@NamedInterface` o eventi. Non importare mai classi dal pacchetto `internal` di un altro modulo.

## 2. Architettura Multi-Tenant (Shared Schema)
L'applicazione adotta una strategia di isolamento dei dati basata su **Shared Schema** con colonna discriminante.

- **Isolamento Hibernate 6:** Le entità tenant-aware devono includere un campo `UUID tenantId` annotato con `@TenantId`. Hibernate aggiunge automaticamente la clausola `WHERE tenant_id = ?` a ogni query SQL.
- **Identificazione (JWT):** Il `tenantId` è veicolato nel token JWT. Il `JwtAuthenticationFilter` lo estrae e lo popola nel `TenantContext`.
- **Gestione Contesto:** Il `TenantContext` utilizza un `ThreadLocal` per memorizzare l'UUID dell'agenzia durante la transazione. Deve essere pulito in un blocco `finally`.
- **TenantIdentifierResolver:** Legge l'ID dal `TenantContext`. Se non è presente un utente loggato, restituisce un `DEFAULT_TENANT` (UUID con soli zeri).
- **Impersonation:** Utenti con permesso `admin:access` possono usare l'header `X-Tenant-ID` per operare nel contesto di uno specifico tenant.

## 3. Validazione a Due Livelli
La validazione deve essere gestita in modo rigoroso e separato:

- **Livello Sintattico (DTO):** Ogni `record` di richiesta deve utilizzare le annotazioni `jakarta.validation` (`@NotBlank`, `@NotNull`, `@Size`, etc.). Nel **Controller**, i parametri di input devono essere annotati con **`@Validated`** (Spring, non `@Valid` Jakarta) per intercettare gli errori prima che raggiungano il service.
- **Livello Business (Validator Bean):** Per controlli complessi che richiedono accesso al database o logiche inter-entità (es. verifica unicità, vincoli di stato), crea un componente `@Component` denominato `[Domain]Validator`. Il Service invoca questo bean come primo step.
- **Pattern Builder:** Per i `record` Java, utilizza l'annotazione `@Builder` di Lombok per facilitare la creazione di istanze nei test e nei mapper, garantendo comunque l'immutabilità.

## 4. Web Layer e Mappers (Gestione PUT & PATCH)

Per ogni dominio e sottodominio, è obbligatorio implementare sia la logica di aggiornamento totale (PUT) che quella parziale (PATCH).

### 4.1. Strategie di Aggiornamento
- **PUT (Full Update):** Sostituzione integrale dello stato dell'entità. Ogni campo del DTO sovrascrive il corrispondente campo dell'entità.
- **PATCH (Partial Update):** Aggiornamento selettivo. Solo i campi presenti (non null) nel DTO devono essere applicati all'entità esistente.

### 4.2. Regole di Implementazione
- **Immutabilità:** Usa esclusivamente `record` Java per tutti i DTO.
- **Mapping Funzionale (NO MapStruct):**
  - **Create/Response:** Implementa `java.util.function.Function<Source, Target>`.
  - **Update (PUT):** Implementa `java.util.function.BiFunction<RequestDto, Entity, Entity>` per la sovrascrittura completa di tutti i campi (controllando sempre quali campi ovviamente, controllati dal validator).
  - **Patch (PATCH):** Implementa `java.util.function.BiFunction<RequestDto, Entity, Entity>`. Il mapper deve applicare i cambiamenti all'entità esistente in modo selettivo (**null-safe**), preservando i dati originali se il campo nel DTO è null.
    - *Esempio logica PATCH:* `if (dto.campo() != null) entity.setCampo(dto.campo());`

### 4.3. Requisiti API
Ogni risorsa deve esporre nel Controller:
- Un endpoint `@GetMapping` paginato (usa `Pageable` + `[Domain]SearchCriteria`) che ritorna `PageResponse<ResponseDto>`.
- Un endpoint `@GetMapping("/list")` non paginato che ritorna `List<ResponseDto>`.
- Un endpoint `@PostMapping` che ritorna `ResponseEntity.created(location).body(dto)` con `Location` header.
- Un endpoint `@PutMapping("/{publicId}")` che utilizza il mapper di update totale.
- Un endpoint `@PatchMapping("/{publicId}")` che utilizza il mapper di update parziale (null-safe).
- Un endpoint `@DeleteMapping("/{publicId}")` che ritorna `ResponseEntity.noContent()`.
- **Risorse figlie** (es. `Unit` dentro `Property`, `Room` dentro `Unit`) usano routing annidato: `/api/v1/properties/{propertyId}/units/{unitId}/rooms`.

### 4.4. OpenAPI (Springdoc)
Ogni Controller deve essere annotato con:
- `@Tag(name = "...", description = "...")` a livello di classe.
- `@Operation(summary = "...")` su ogni metodo endpoint.
- `@ParameterObject` su `Pageable` e `[Domain]SearchCriteria` per la corretta generazione della documentazione.

### 4.5. Paginazione e Filtro
- **`PageResponse<T>`** (`it.andrea.insula.core.dto.PageResponse`): wrapper di risposta paginata con `content` e `page` (`PageMetadata`). Si costruisce via `PageResponse.fromPage(page.map(responseMapper))`.
- **`[Domain]SearchCriteria`**: `record` con campi opzionali usato come query parameter (`@ParameterObject`). Nessun campo obbligatorio.
- **`[Domain]Specification`**: classe con metodo statico `withCriteria(criteria)` che restituisce un `Specification<Entity>` per JPA. Usa `cb.conjunction()` come base e aggiunge predicati solo se i campi non sono null/blank. Per entità con soft delete, di default escludi i record con status `DELETED`.

## 5. Standard Database e JPA
- **Gerarchia BaseEntity:** Le entità devono estendere le classi base appropriate per ereditare l'auditing e l'identificazione (es. `BaseEntity`, `TenantAwareBaseEntity`). Per facilitare i controlli di sicurezza, propaga coerentemente l'estensione di `TenantAwareBaseEntity` anche alle entità figlie strette (es. `CustomerAddress` appartenente a un `BusinessCustomer`).
- **Doppia Chiave e publicId:** - `Long id` (@Id) è usato internamente per le relazioni a DB e per le performance.
  - `UUID publicId` funge da identificatore opaco ed è l'unico da esporre nei DTO e nelle API REST (es. `/api/resource/{publicId}`).
  - **Inizializzazione Java (Mandatoria):** L'UUID deve essere **sempre inizializzato direttamente in Java** al momento dell'istanziazione dell'oggetto (es. `private UUID publicId = UUID.randomUUID();`). **Vietato l'uso di `@UuidGenerator`** o la delega della generazione a Hibernate/Database. Questo garantisce un identificatore di business forte e immutabile fin dal primo istante, evitando bug sulle Hash Collections prima del salvataggio.
  - **Consistenza e DRY:** Centralizza la definizione e l'inizializzazione del `publicId` nelle classi base (es. creando una `PublicBaseEntity` o aggiungendolo in `BaseEntity`) per evitare duplicazioni. Usa sempre la sintassi coerente: `@Column(name = "public_id", nullable = false, unique = true, updatable = false)`.
  - **Eccezioni al publicId:** Le entità che possiedono già una chiave pubblica di business naturale (es. `Permission` con `authority`, o tabelle di dizionario/ruoli statici) possono omettere l'uso del `publicId` e utilizzare direttamente la chiave naturale.
- **equals() e hashCode() (Fondamentale):**
  - **MAI** basare `equals()` e `hashCode()` sull'`id` del database (`Long id`). Poiché l'ID primario viene generato solo dopo l'inserimento sul DB, il suo hash cambierebbe dopo la persistenza, causando bug critici e perdite di dati all'interno delle Collection (es. `HashSet`) di Hibernate.
  - Utilizza **sempre e solo** il `publicId` (che, essendo inizializzato in Java alla creazione, non è mai `null`) oppure campi univoci di business (es. `email`, `username`, `authority`, `name`) per l'implementazione di `equals()` e `hashCode()`.
- **Prevenzione rigorosa del Problema N+1 (ATTENZIONE COSTANTE):**
  - Presta sempre la massima attenzione alle query N+1, specialmente durante la fase di mapping. **È severamente vietato** chiamare metodi su collezioni Lazy (come `.size()`, es. `property.getUnits().size()`) o iterare su collezioni non inizializzate all'interno dei Mapper. Questo nasconde insidie prestazionali gravi (es. 1 query principale + N query aggiuntive in un ciclo `findAll`).
  - **Soluzioni imposte:** Usa query JPQL mirate con `JOIN FETCH`, annotazioni `@EntityGraph` nei Repository, subquery native, colonne calcolate con `@Formula`, oppure esegui query di `COUNT` dedicate *prima* del mapping e passale al mapper.
- **Soft Delete:** Le entità con ciclo di vita (es. `User`, `Agency`) usano un enum `[Domain]Status` (che include `DELETED`) + un campo `Instant deletedAt` + un metodo `delete()` che imposta entrambi. Non cancellare mai fisicamente questi record. Nelle `Specification`, escludi di default i `DELETED`.
- **JPA Inheritance:** Per entità con sottotipi (es. `Customer`), usa `@Inheritance(strategy = InheritanceType.JOINED)` con `@DiscriminatorColumn` sulla superclasse e `@DiscriminatorValue` sulle sottoclassi.
- **Custom Converters:** Per tipi non supportati nativamente da JPA (es. `ZoneId`), crea un `@Converter` che implementa `AttributeConverter<JavaType, String>`, posizionato nel package `model` del dominio (es. `ZoneIdAttributeConverter`).

## 6. Sicurezza e Error Handling
- **Autorizzazioni:** Ogni endpoint deve avere `@PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.DOMAIN_ACTION + "')")`. Usa sempre le costanti dalla classe `PermissionAuthority.Constants` (`it.andrea.insula.security.PermissionAuthority`) — non hardcodare stringhe.
- **`AdminGuard`:** Componente `it.andrea.insula.user.internal.user.service.AdminGuard` per centralizzare i controlli sull'utente system admin (identificato dal flag `systemAdmin`, non dal ruolo). Usa `adminGuard.assertNotAdmin(user)` prima di qualunque modifica/cancellazione di utente.
- **Eccezioni — Gerarchia concreta:**
  Lancia sempre una sottoclasse di `BaseLocalizedException` appropriata al contesto:
  | Classe | HTTP Status | Uso tipico |
  |---|---|---|
  | `ResourceNotFoundException` | 404 | Entità non trovata per `publicId` |
  | `ResourceInUseException` | 409 | Eliminazione bloccata da dipendenze |
  | `ImmutableResourceException` | 403 | Tentativo di modifica di risorsa immutabile (es. admin) |
  | `BusinessRuleException` | 422 | Violazione di regola di business |
  Tutte si trovano in `it.andrea.insula.core.exception`.
- **Enum errori:** Definisci i codici errore in un Enum `[Domain]ErrorCodes` che implementa `ErrorDefinition`. Convenzione numerica dei codici:
  - `99xxx` — errori comuni (`CommonErrorCodes`)
  - `1xxx` — modulo `user`
  - `2xxx` — modulo `customer`
  - `3xxx` — modulo `agency`
  - `4xxx` — modulo `property`
- **Enum i18n (`EnumTranslator`):** Quando un enum viene esposto in un DTO di risposta, traducilo tramite `EnumTranslator` (iniettabile, `it.andrea.insula.core.dto.EnumTranslator`) che restituisce un `TranslatedEnum(code, label)`. La chiave di traduzione segue la convenzione `enum.<lowercaseclassname>.<VALUE>` in `messages.properties` (es. `enum.agencystatus.ACTIVE`).

## 7. Workflow Operativo (Mandatorio)
1. **Analisi:** Verifica la necessità di `@TenantId`, definisci i vincoli di validazione e **valuta in anticipo il rischio di query N+1** nelle relazioni dell'entità.
2. **Pianificazione:** Elenca i file: Entity, `[Domain]ErrorCodes`, Validator, Mappers, Service, Controller. Aggiungi `[Domain]Specification` e `[Domain]SearchCriteria` se serve filtro/paginazione.
3. **Implementazione:**
   - Applica `@Validated` nel Controller (non `@Valid`).
   - Implementa il **Validator** per i controlli di business.
   - Gestisci la logica di fusione dati esclusivamente nel **Patch Mapper**.
   - Usa `@RequiredArgsConstructor` e `private final` per la Dependency Injection.
   - Aggiungi le chiavi i18n in `messages.properties` (e nelle varianti `_it`, `_en`).
   - Registra le nuove autorizzazioni in `PermissionAuthority` e `PermissionAuthority.Constants`.

## 8. Esempio Struttura
```text
it.andrea.insula.modulo.internal.subdomain
 ├── dto
 │    ├── request
 │    │    ├── SubdomainCreateDto.java (record)
 │    │    ├── SubdomainUpdateDto.java (record, per PUT)
 │    │    ├── SubdomainPatchDto.java (record, per PATCH)
 │    │    └── SubdomainSearchCriteria.java (record, campi opzionali)
 │    └── response
 │         └── SubdomainResponseDto.java (record)
 ├── exception
 │    └── SubdomainErrorCodes.java (enum implements ErrorDefinition)
 ├── mapper
 │    ├── SubdomainCreateMapper.java (Function<CreateDto, Entity>)
 │    ├── SubdomainUpdateMapper.java (BiFunction<UpdateDto, Entity, Entity>)
 │    ├── SubdomainPatchMapper.java (BiFunction<PatchDto, Entity, Entity>)
 │    └── SubdomainResponseMapper.java (Function<Entity, ResponseDto>)
 ├── model
 │    ├── Subdomain.java (@Entity con @TenantId, soft delete se necessario)
 │    ├── SubdomainRepository.java (JpaRepository + JpaSpecificationExecutor)
 │    └── SubdomainSpecification.java (Specification builder)
 ├── service
 │    ├── SubdomainService.java (orchestra validator e mapper)
 │    └── SubdomainValidator.java (controlli business/DB)
 └── web
      └── SubdomainController.java (@Tag, @Operation, @Validated, @ParameterObject)