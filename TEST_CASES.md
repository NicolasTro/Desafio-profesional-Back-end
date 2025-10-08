# LINK A TESTS

https://docs.google.com/spreadsheets/d/1SXlsOfC-JlmyOVQh0Jlj-UF23aEULhzBB7RZ5ZvcjNI/edit?usp=sharing

# LINK A JIRA

https://nikolastroupkos.atlassian.net/jira/software/projects/DB/boards/35/backlog?epics=visible&sprintCompleted=&atlOrigin=eyJpIjoiYTcwYzZlNzhhNWZkNDRmNWEwMmRhNGM3N2YyOWZlNmEiLCJwIjoiaiJ9


# Diseños de casos de prueba

## Sección: Auth Service

Endpoint: POST /auth/login

- ID: AUTH-01
  - Título: Login exitoso
  - Descripción: Verificar que un usuario con credenciales válidas recibe un token JWT
  - Precondición: Usuario existente en BD con email y password en BCrypt
  - #Paso: 1
  - Pasos: POST /auth/login con payload {"email":"juan@example.com","password":"123456"}
  - Resultado esperado: 200 OK, body { "token": "<jwt>" }
  - Tipo: Funcional
  - Nivel: integracion
  - Suite: smoke
  - Estado de diseño: Automatizado (`AuthIntegrationTest.login_shouldReturn200_whenCredentialsAreValid`)
  - Entorno: test

- ID: AUTH-02
  - Título: Login con contraseña inválida
  - Descripción: Verificar que credenciales incorrectas devuelven 401
  - Precondición: Usuario existente
  - #Paso: 1
  - Pasos: POST /auth/login con contraseña incorrecta
  - Resultado esperado: 401 Unauthorized, body con campo error
  - Tipo: Funcional
  - Nivel: integracion
  - Suite: regression
  - Estado de diseño: Automatizado (`AuthIntegrationTest.login_shouldReturn401_whenCredentialsAreInvalid`)
  - Entorno: test

- ID: AUTH-03
  - Título: Login usuario inexistente
  - Descripción: Verificar comportamiento cuando el email no está registrado
  - Precondición: No existe usuario con el email
  - #Paso: 1
  - Pasos: POST /auth/login con email inexistente
  - Resultado esperado: 404/401 según manejo de excepciones, con mensaje de error
  - Tipo: Funcional
  - Nivel: componentes / integracion
  - Suite: regression
  - Estado de diseño: Parcialmente automatizado (`AuthServiceTest.login_shouldThrowException_whenUserNotFound` - unit)
  - Entorno: test

Endpoint: POST /auth/logout

- ID: AUTH-04
  - Título: Logout exitoso
  - Descripción: Verificar que logout con token válido devuelve mensaje de éxito
  - Precondición: Header Authorization con token válido
  - #Paso: 1
  - Pasos: POST /auth/logout con header Authorization: valid.jwt.token
  - Resultado esperado: 200 OK, { "message": "Logout exitoso para <email>" }
  - Tipo: Funcional
  - Nivel: componentes
  - Suite: smoke
  - Estado de diseño: Automatizado (`AuthControllerTest.logout_withBearerToken_returnsOk`)
  - Entorno: test

- ID: AUTH-05
  - Título: Logout con token inválido
  - Descripción: Verificar que token inválido devuelve 401
  - Precondición: Header Authorization presente pero JwtUtil lanza excepción
  - #Paso: 1
  - Pasos: POST /auth/logout con header Authorization: invalid.token
  - Resultado esperado: 401 Unauthorized, body con error "Token inválido"
  - Tipo: Funcional
  - Nivel: componentes
  - Suite: regression
  - Estado de diseño: Automatizado (`AuthControllerTest.logout_withInvalidToken_returnsUnauthorized`)
  - Entorno: test

- ID: AUTH-06
  - Título: Logout sin header Authorization
  - Descripción: Verificar que ausencia de header devuelve error
  - Precondición: Ninguna
  - #Paso: 1
  - Pasos: POST /auth/logout sin header Authorization
  - Resultado esperado: 401 Unauthorized, body con error
  - Tipo: Funcional
  - Nivel: componentes
  - Suite: regression
  - Estado de diseño: Automatizado (`AuthControllerTest.logout_missingHeader_returnsUnauthorized`)
  - Entorno: test


## Sección: Users Service

Endpoint: POST /users/register

- ID: USER-01
  - Título: Registro exitoso de usuario
  - Descripción: Registrar un usuario con todos los campos válidos; verificar CVU y alias generados
  - Precondición: No existe usuario con el email dado; `words.txt` disponible en resources
  - #Paso: 1
  - Pasos: POST /users/register con payload válido
  - Resultado esperado: 200 OK; response contiene user con `cvu` de 22 dígitos y `alias` en formato word.word.word
  - Tipo: Funcional
  - Nivel: integracion
  - Suite: smoke
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test

- ID: USER-02
  - Título: Registro falla por email vacío
  - Descripción: Verificar que email nulo o vacío produce ValidationException
  - Precondición: Ninguna
  - #Paso: 1
  - Pasos: POST /users/register con email = ""
  - Resultado esperado: 400 Bad Request con mensaje "El email no puede ser nulo o vacío"
  - Tipo: Funcional
  - Nivel: componentes / integracion
  - Suite: regression
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test

- ID: USER-03
  - Título: Registro falla por password corta
  - Descripción: Verificar que password menor a 6 caracteres produce ValidationException
  - Precondición: Ninguna
  - #Paso: 1
  - Pasos: POST /users/register con password = "123"
  - Resultado esperado: 400 Bad Request con mensaje "La contraseña debe tener al menos 6 caracteres"
  - Tipo: Funcional
  - Nivel: componentes
  - Suite: regression
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test

- ID: USER-04
  - Título: Registro falla si email ya registrado
  - Descripción: Verificar que al intentar registrar un email existente se lanza ValidationException
  - Precondición: Existe usuario en repositorio con ese email
  - #Paso: 1
  - Pasos: POST /users/register con email duplicado
  - Resultado esperado: 400 Bad Request con mensaje "El email ya está registrado"
  - Tipo: Funcional
  - Nivel: componentes
  - Suite: regression
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test

- ID: USER-05
  - Título: Generación de CVU
  - Descripción: Verificar que `generateCvu` genera una cadena de 22 dígitos
  - Precondición: Ninguna
  - #Paso: 1
  - Pasos: Invocar internamente `generateCvu` (unit test)
  - Resultado esperado: String.length == 22 y contiene solo dígitos
  - Tipo: Funcional
  - Nivel: componentes
  - Suite: regression
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: local/test

- ID: USER-06
  - Título: Comportamiento cuando `words.txt` está vacío
  - Descripción: Verificar que `generateAlias` lanza RuntimeException si no hay palabras disponibles
  - Precondición: `words.txt` vacío o no cargable
  - #Paso: 1
  - Pasos: Forzar carga de `words.txt` vacía y llamar `generateAlias`
  - Resultado esperado: RuntimeException con mensaje "No hay palabras disponibles en words.txt"
  - Tipo: Funcional
  - Nivel: componentes
  - Suite: regression
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: local/test


## Sección: Accounts Service

Endpoint: POST /accounts

- ID: ACC-01
  - Título: Crear cuenta exitosamente
  - Descripción: Verificar que se crea una cuenta asociada a un usuario
  - Precondición: Usuario existente
  - #Paso: 1
  - Pasos: POST /accounts con userId válido
  - Resultado esperado: 200 OK, cuenta creada con cvu
  - Tipo: Funcional
  - Nivel: integracion
  - Suite: smoke
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test

Endpoint: GET /accounts/{cvu}

- ID: ACC-02
  - Título: Obtener cuenta por CVU
  - Descripción: Verificar que se obtiene la cuenta correcta por CVU
  - Precondición: Cuenta existente
  - #Paso: 1
  - Pasos: GET /accounts/{cvu} con cvu válido
  - Resultado esperado: 200 OK, datos de la cuenta
  - Tipo: Funcional
  - Nivel: integracion
  - Suite: smoke
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test

Endpoint: GET /accounts/user/{userId}

- ID: ACC-03
  - Título: Obtener cuentas por usuario
  - Descripción: Verificar que se obtienen las cuentas de un usuario
  - Precondición: Usuario con cuentas
  - #Paso: 1
  - Pasos: GET /accounts/user/{userId}
  - Resultado esperado: 200 OK, lista de cuentas
  - Tipo: Funcional
  - Nivel: integracion
  - Suite: smoke
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test


## Sección: Cards Service

Endpoint: GET /cards/{cvu}/cards

- ID: CARD-01
  - Título: Obtener tarjetas de una cuenta
  - Descripción: Verificar que se obtienen las tarjetas asociadas a un CVU
  - Precondición: Cuenta con tarjetas
  - #Paso: 1
  - Pasos: GET /cards/{cvu}/cards
  - Resultado esperado: 200 OK, lista de tarjetas
  - Tipo: Funcional
  - Nivel: integracion
  - Suite: smoke
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test

Endpoint: POST /cards/{cvu}

- ID: CARD-02
  - Título: Crear tarjeta para una cuenta
  - Descripción: Verificar que se crea una tarjeta asociada a un CVU
  - Precondición: Cuenta existente
  - #Paso: 1
  - Pasos: POST /cards/{cvu} con datos de tarjeta
  - Resultado esperado: 200 OK, tarjeta creada
  - Tipo: Funcional
  - Nivel: integracion
  - Suite: smoke
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test


## Sección: Transactions Service

Endpoint: GET /transactions/{accountId}

- ID: TRANS-01
  - Título: Obtener transacciones de una cuenta
  - Descripción: Verificar que se obtienen las transacciones de una cuenta
  - Precondición: Cuenta con transacciones
  - #Paso: 1
  - Pasos: GET /transactions/{accountId}
  - Resultado esperado: 200 OK, lista de transacciones
  - Tipo: Funcional
  - Nivel: integracion
  - Suite: smoke
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test

Endpoint: POST /transactions

- ID: TRANS-02
  - Título: Crear transacción
  - Descripción: Verificar que se crea una transacción entre cuentas
  - Precondición: Cuentas origen y destino existentes
  - #Paso: 1
  - Pasos: POST /transactions con datos de transacción
  - Resultado esperado: 200 OK, transacción creada
  - Tipo: Funcional
  - Nivel: integracion
  - Suite: smoke
  - Estado de diseño: Diseñado (Pendiente de automatización)
  - Entorno: test


### Resultados por clase 

Auth-service:

- `AuthControllerTest` -> Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 (PASS)
- `AuthServiceTest` -> Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 (PASS)

Users-service:

- `UserControllerTest` -> Tests run: 1, Failures: 0, Errors: 0, Skipped: 0 (PASS)
- `UserServiceTest` -> Tests run: 7, Failures: 0, Errors: 0, Skipped: 0 (PASS)

Accounts-service:

- `AccountsIntegrationTest` -> (PASS)
- `AccountsServiceTest` -> (PASS)
- `AccountsControllerTest` -> (PASS)

Cards-service:

- `CardControllerTest` -> (PASS)
- `CardServiceTest` -> (PASS)
- `CardRepositoryTest` -> (PASS)
- `SmokeTest` (cards-service) -> (PASS)

Transactions-service:

- `TransactionControllerTest` -> (PASS)
- `TransactionServiceTest` -> (PASS)

Auth-service (additional):

- `AuthIntegrationTest` -> (PASS)
- `AuthSmokeTest` -> (PASS)

Users-service (additional):

- `UserIntegrationTest` -> (PASS)
- `UserSmokeTest` -> (PASS)


## Ejecución detallada por caso de prueba

A continuación se registran, por cada caso, solo los campos solicitados: Fecha de ejecución, Estado de ejecución, Resultado obtenido, Suite, Entorno, Ejecutor y Observaciones.

- ID: AUTH-01
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: 200 OK; token JWT devuelto y validado por las aserciones.
  - Suite: AuthControllerTest (unit) / AuthIntegrationTest (integration) - humo
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven (PowerShell). Tests run: 5, Failures: 0. Algunas ejecuciones agrupadas mostraron problemas de escritura de informes Surefire en `target/surefire-reports`; se usaron ejecuciones por clase para evidencia.

- ID: AUTH-02
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: 401 Unauthorized cuando la contraseña es incorrecta; body contiene campo de error.
  - Suite: AuthControllerTest (unit) - regresion
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven. Tests run: 5, Failures: 0.

- ID: AUTH-03
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: RuntimeException lanzada al intentar autenticar un usuario inexistente (comportamiento verificado en unit test).
  - Suite: AuthServiceTest (unit) - regresion
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven. Tests run: 3, Failures: 0.

- ID: AUTH-04
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: 200 OK; logout exitoso y mensaje esperado.
  - Suite: AuthControllerTest (unit) - humo
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven con JwtUtil mockeado. Tests run: 5, Failures: 0.

- ID: AUTH-05
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: 401 Unauthorized cuando el token es inválido; body con mensaje de error.
  - Suite: AuthControllerTest (unit) - regresion
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven. Tests run: 5, Failures: 0.

- ID: AUTH-06
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: 401 Unauthorized cuando falta el header Authorization.
  - Suite: AuthControllerTest (unit) - regresion
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven. Tests run: 5, Failures: 0.

- ID: USER-01
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: 200 OK; usuario devuelto con `cvu` (22 dígitos) y `alias` (3 palabras separadas por puntos).
  - Suite: UserControllerTest (unit) / UserServiceTest (unit) - humo
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven; UserControllerTest: Tests run: 1, UserServiceTest: Tests run: 7; ambos sin fallos.

- ID: USER-02
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: 400 Bad Request por email vacío (ValidationException lanzada en lógica de validación).
  - Suite: UserServiceTest (unit) - regresion
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven. Tests run: 7, Failures: 0.

- ID: USER-03
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: 400 Bad Request por password corta (ValidationException lanzada).
  - Suite: UserServiceTest (unit) - regresion
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven. Tests run: 7, Failures: 0.

- ID: USER-04
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: 400 Bad Request por email duplicado (ValidationException lanzada cuando repo encuentra el email).
  - Suite: UserServiceTest (unit) - regresion
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven. Tests run: 7, Failures: 0.

- ID: USER-05
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PASADO
  - Resultado obtenido: CVU generado de 22 dígitos verificado por aserciones unitarias.
  - Suite: UserServiceTest (unit) - regresion
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven. Tests run: 7, Failures: 0.

- ID: USER-06
  - Fecha de ejecución: 2025-09-16
  - Estado de ejecución: PARCIAL
  - Resultado obtenido: Comportamiento esperado (RuntimeException) considerado cubierto por tests unitarios que simulan la ausencia de palabras; falta test específico que fuerce `words.txt` vacío en resources para validar el caso real.
  - Suite: UserServiceTest (unit)
  - Entorno: test
  - Ejecutor: Nicolas Troupkos
  - Observaciones: Ejecutado vía Maven. Tests run: 7, Failures: 0. Recomendado: añadir test que load-e `words.txt` vacío desde resources para cubrir definitivamente este caso.

