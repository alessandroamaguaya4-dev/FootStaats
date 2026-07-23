# 🏃‍♂️ FootStaats

Aplicación Android para llevar un registro de estadísticas de actividad física, monitorear el progreso deportivo y fomentar hábitos saludables mediante gamificación.

---

# 📋 Descripción del problema

El desarrollo de la aplicación móvil FootStaats responde a una necesidad real y poco atendida: la ausencia de herramientas de seguimiento estadístico individual para jugadores de fútbol amateur.

Mientras que el fútbol profesional dispone de cuerpos técnicos especializados y plataformas avanzadas de análisis de rendimiento, los jugadores de ligas barriales carecen de sistemas que les permitan registrar y visualizar su evolución deportiva.

FootStaats busca cerrar esta brecha ofreciendo una solución móvil accesible, funcional incluso en entornos sin conectividad y orientada exclusivamente al jugador individual.

---

# 🎯 Objetivo de la aplicación

Diseñar e implementar una aplicación móvil Android que permita a jugadores de fútbol amateur registrar sus estadísticas individuales y visualizar su evolución deportiva mediante una gráfica radar, con el fin de apoyar la toma de decisiones personales sobre entrenamiento y rendimiento.

---

# ⚽ FootStaats

Aplicación móvil de estadísticas deportivas para jugadores de fútbol amateur.

FootStaats permite registrar y visualizar estadísticas individuales de partidos, entrenamientos y lesiones, integrando un perfil personal con métricas dinámicas y una gráfica de rendimiento tipo radar.

---

# 📱 Capturas de pantalla

Para visualizar la aplicación en funcionamiento y validar los flujos de interfaz:

- **Bienvenida / Registro / Perfil:**  
  Interfaz principal donde se visualizan estadísticas consolidadas del jugador y su balance deportivo mediante una gráfica radar (`image_bfe15f.jpg`).

- **Configuración del sistema y permisos:**  
  Estado de los interruptores de notificaciones locales dentro de la configuración del dispositivo (`image_bfe8bd.png`, `image_bfeca3.jpg`).

- **Entorno de desarrollo y depuración:**  
  Solución a las directivas de seguridad necesarias para la compilación correcta de canales de alertas locales (`image_bff70b.png`, `image_bff804.jpg`, `image_bffbc8.jpg`, `image_bfff87.png`).

---

# 🚀 Funcionalidades

- 🔐 Registro e inicio de sesión mediante correo electrónico y contraseña.
- ✅ Validación de formularios utilizando expresiones regulares (`Patterns.EMAIL_ADDRESS`).
- 👤 Perfil del jugador con fotografía, nombre y posición.
- 📊 Visualización dinámica de estadísticas deportivas.
- ⚽ Registro de partidos, entrenamientos y lesiones.
- 📈 Gráfica radar interactiva para representar el rendimiento del jugador.
- 🔔 Sistema de notificaciones locales y recordatorios deportivos.
- 💾 Persistencia local de datos utilizando Room Database.
- 📱 Funcionamiento offline sin necesidad de conexión a internet.

---

# 🛠️ Tecnologías utilizadas

| Tecnología | Uso |
| :--- | :--- |
| **Java** | Lenguaje principal de desarrollo |
| **Android Studio** | Entorno de desarrollo (IDE) |
| **Room Database** | Persistencia local de datos (SQLite) |
| **Glide 4.16** | Carga y visualización de foto de perfil |
| **Material Design 3** | Componentes modernos de interfaz |
| **LiveData / ViewModel** | Arquitectura reactiva |
| **SharedPreferences** | Gestión de sesión |
| **Executors** | Procesamiento asíncrono |
| **MPAndroidChart** | Renderizado de gráfica radar |

---

# 🏗️ Arquitectura del proyecto

El proyecto está construido bajo una estructura modular limpia, organizando el código dentro del paquete `com.example.footstaats` para asegurar desacoplamiento entre la lógica de negocio, persistencia y presentación.

```text
com.example.footstaats/
├── data/
│   ├── dao/               # Interfaces de acceso a datos (Room)
│   │   ├── PartidoDao
│   │   ├── EntrenamientoDao
│   │   ├── LesionDao
│   │   └── UsuarioDao
│   ├── db/
│   │   └── AppDatabase    # Base de datos Room (versión 3)
│   └── model/             # Entidades de la base de datos
│       ├── Partido
│       ├── Entrenamiento
│       ├── Lesion
│       └── Usuario
├── repository/
│   └── EstadisticasRepository
└── ui/
    ├── SplashActivity
    ├── LoginActivity
    ├── RegisterActivity
    ├── MainActivity
    ├── RegistroPartidoActivity
    ├── RegistroEntrenamientoActivity
    ├── RegistroLesionActivity
    └── HistorialActivity
    🧩 Componentes clave
🎨 Capa de Presentación (.ui)

Administra los elementos visuales basados en Material Design 3.

Utiliza la librería MPAndroidChart mediante el componente RadarChart para representar matemáticamente las métricas deportivas del jugador de manera radial y cíclica.

💾 Capa de Persistencia (.data)

Responsable del almacenamiento local y la consistencia de los datos utilizando Room ORM sobre SQLite embebido.

Las operaciones de acceso a datos se aíslan en subprocesos asíncronos mediante Executors para evitar bloqueos en el hilo principal de la interfaz.

🗄️ Modelo de base de datos
Tabla: usuarios
Campo	Tipo	Descripción
id	INTEGER PK	Identificador autoincremental
nombre	TEXT	Nombre completo del jugador
correo	TEXT	Correo electrónico único
contrasena	TEXT	Contraseña del usuario
posicion	TEXT	Posición en el campo
edad	TEXT	Edad del jugador
foto	TEXT	URI de la foto de perfil
Tabla: partidos
Campo	Tipo	Descripción
id	INTEGER PK	Identificador autoincremental
fecha	TEXT	Fecha del partido
rival	TEXT	Nombre del equipo rival
goles	INTEGER	Goles anotados
asistencias	INTEGER	Asistencias realizadas
minutosJugados	INTEGER	Minutos en cancha
tarjetas	TEXT	Tipo de tarjeta recibida
posicion	TEXT	Posición jugada
notas	TEXT	Observaciones adicionales
Tabla: entrenamientos
Campo	Tipo	Descripción
id	INTEGER PK	Identificador autoincremental
fecha	TEXT	Fecha del entrenamiento
tipo	TEXT	Tipo de entrenamiento
duracionMinutos	INTEGER	Duración en minutos
golesEntrenamiento	INTEGER	Goles anotados
intensidad	INTEGER	Intensidad de 1 a 5
descripcion	TEXT	Descripción del entrenamiento
Tabla: lesiones
Campo	Tipo	Descripción
id	INTEGER PK	Identificador autoincremental
fecha	TEXT	Fecha de la lesión
tipo	TEXT	Tipo de lesión
parteCuerpo	TEXT	Parte afectada
diasRecuperacion	INTEGER	Días estimados de recuperación
descripcion	TEXT	Descripción de la lesión
🔐 Validaciones implementadas

FootStaats utiliza expresiones regulares y validaciones lógicas antes de realizar transacciones en la base de datos local.

Campo	Validación	Regex / Método
Correo electrónico	Formato válido	Patterns.EMAIL_ADDRESS
Confirmación de correo	Coincidencia exacta	Comparación de strings
Contraseña	Seguridad básica	length() >= 6
Nombre	Presencia obligatoria	!isEmpty()
Nota técnica

El patrón interno utilizado por Patterns.EMAIL_ADDRESS corresponde aproximadamente a:

[a-zA-Z0-9+._%-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9-]{0,64}(\.[a-zA-Z0-9][a-zA-Z0-9-]{0,64})+
🔔 Sistema de alertas y notificaciones locales

La aplicación incorpora un sistema de recordatorios locales asíncronos enfocado en mantener la consistencia deportiva del atleta.

⚙️ Mecanismo de disparo

El sistema se encuentra vinculado al ciclo de vida en primer plano (onCreate) de la pantalla principal.

Al inicializar la interfaz, la aplicación evalúa dinámicamente el estado de los permisos necesarios para emitir notificaciones locales.

📢 Notification Channels

Implementación obligatoria de canales de alta prioridad (IMPORTANCE_HIGH) compatible con Android 13+ mediante el permiso:

POST_NOTIFICATIONS

Esto garantiza:

Emisión sonora de notificaciones.
Alertas flotantes.
Compatibilidad con capas restrictivas como MagicOS o MIUI.
⚙️ Requisitos previos
Android Studio Hedgehog o superior.
JDK 17 o superior (desarrollado y probado bajo JDK 23).
Android SDK API 24 (Android 7.0 Nougat) o superior.
Dispositivo Android físico o emulador compatible.
📦 Instalación
1️⃣ Clonar el repositorio
git clone https://github.com/tuusuario/FootStaats.git
2️⃣ Abrir el proyecto

Abre el proyecto dentro de Android Studio.

3️⃣ Sincronizar dependencias

Espera a que Gradle descargue y sincronice todas las dependencias necesarias.

4️⃣ Conectar dispositivo Android

Conecta tu dispositivo mediante depuración USB.

Nota: Si utilizas dispositivos Honor o Xiaomi, habilita la instalación de aplicaciones vía USB.

5️⃣ Ejecutar la aplicación

Presiona el botón ▶ Run desde la barra superior del IDE para compilar e instalar la aplicación.

📋 Dependencias principales
// Room - Base de datos local
implementation "androidx.room:room-runtime:2.6.1"
annotationProcessor "androidx.room:room-compiler:2.6.1"

// ViewModel y LiveData - Arquitectura Reactiva
implementation "androidx.lifecycle:lifecycle-viewmodel:2.7.0"
implementation "androidx.lifecycle:lifecycle-livedata:2.7.0"

// Glide - Manejo de imágenes
implementation "com.github.bumptech.glide:glide:4.16.0"

// MPAndroidChart - Gráfica radar
implementation "com.github.PhilJay:MPAndroidChart:v3.1.0"
👤 Autor

Alessandro Amaguaya
Estudiante de Ingeniería en Computación
Universidad Central del Ecuador — Facultad de Ingeniería y Ciencias Aplicadas

📄 Licencia

Este proyecto fue desarrollado con fines estrictamente académicos para la materia de Metodología de la Investigación.
