# FootStaats ⚽📈

**FootStaats** es una aplicación móvil nativa para Android diseñada para futbolistas amateur que buscan llevar un control riguroso de su rendimiento individual en partidos y entrenamientos. A través de una interfaz limpia y visualmente atractiva, la plataforma permite registrar métricas clave y visualizar el progreso mediante un gráfico de radar dinámico.

---

## 📋 Descripción del Problema

El desarrollo de la aplicación móvil FootStaats responde a una necesidad real y poco atendida: la ausencia de herramientas de seguimiento estadístico individual para jugadores de fútbol amateur. Mientras que el fútbol profesional dispone de cuerpos técnicos especializados y plataformas de análisis de rendimiento, los jugadores de ligas barriales carecen de cualquier sistema que les permita registrar y visualizar su evolución deportiva. FootStaats busca cerrar esta brecha, ofreciendo una solución móvil accesible, funcional en entornos sin conectividad y orientada exclusivamente al jugador individual.

## 🎯 Objetivo de la Aplicación

Diseñar e implementar una aplicación móvil Android que permita a jugadores de fútbol amateur registrar sus estadísticas individuales y visualizar su evolución deportiva mediante una gráfica radar, con el fin de apoyar la toma de decisiones personales sobre entrenamiento y rendimiento.

---

## 📸 Capturas de la Aplicación

Para ver la aplicación en acción y validar los flujos de su interfaz, consulta las siguientes capturas de pantalla:

* **Panel de Control y Gráfico de Rendimiento:** Interfaz principal donde se visualizan las estadísticas consolidadas del jugador y su balance de juego en el gráfico de radar (`image_bfe15f.jpg`).
* **Configuración del Sistema y Permisos:** Estado de los interruptores de notificaciones locales de la app en la sección de control del dispositivo (`image_bfe8bd.png`, `image_bfeca3.jpg`).
* **Entorno de Desarrollo y Depuración:** Solución a las directivas de seguridad para la compilación exitosa de los canales de alertas locales (`image_bff70b.png`, `image_bff804.jpg`, `image_bffbc8.jpg`, `image_bfff87.png`).

---

## 🚀 Funcionalidades

* 🔐 **Autenticación Segura:** Registro e inicio de sesión con correo electrónico y contraseña.
* ✅ **Validación Avanzada:** Control de entrada en formularios usando expresiones regulares de Android.
* 👤 **Perfil Personalizado:** Visualización del jugador con foto, nombre y posición en la cancha.
* 📊 **Métricas en Tiempo Real:** Renderizado interactivo de estadísticas de juego, minutos, entrenos y goles.

---

## 🏛️ Arquitectura del Proyecto

El proyecto está construido bajo una estructura modular limpia, organizando el código en paquetes dentro de `com.example.footstaats` para asegurar un desacoplamiento eficiente entre la lógica de negocio, los hilos de fondo y la interfaz de usuario:

```text
com.example.footstaats/
├── data/
│   ├── dao/               # Interfaces de acceso a datos (Room)
│   │   ├── PartidoDao
│   │   ├── EntrenamientoDao
│   │   ├── LesionDao
│   │   └── UsuarioDao
│   ├── db/
│   │   └── AppDatabase    # Base de datos Room
│   └── model/             # Entidades declarativas puras
│       ├── Partido
│       ├── Entrenamiento
│       ├── Lesion
│       └── Usuario
├── repository/
│   └── EstadisticasRepository
└── ui/                    # Capa de Presentación (Ciclo de vida y Vistas)
    ├── SplashActivity     # Pantalla de bienvenida
    ├── LoginActivity      # Inicio de sesión
    ├── RegisterActivity   # Registro de usuario
    ├── MainActivity       # Perfil + orquestación de gráfica radar e hilos secundarios
    ├── RegistroPartidoActivity
    ├── RegistroEntrenamientoActivity
    ├── RegistroLesionActivity
    └── HistorialActivity
