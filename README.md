# RetoEcoTik — Aplicación Móvil de Gestión y Gamificación Ecológica

**RetoEcoTik** es una plataforma móvil nativa para Android diseñada para incentivar, registrar y recompensar el reciclaje comunitario mediante mecánicas de gamificación, cálculo de ecopuntos y estimación del impacto ambiental (reducción de emisiones de CO₂).

---

## 1. Características Principales

- **Registro y Pesaje de Residuos:** Módulo optimizado para registrar entregas de residuos clasificados (Plástico PET, Vidrio, Papel/Cartón, Metal y Orgánico Compostable).
- **Cálculo Determinista de Ecopuntos:** Algoritmo de conversión según el tipo de material y pesaje en kilogramos, con acreditación instantánea al balance del usuario.
- **Estimación de Huella de Carbono:** Cálculo en tiempo real de los kilogramos de CO₂ evitados por cada entrega efectuada.
- **Historial de Entregas:** Consulta detallada de todas las transacciones realizadas con fecha, ubicación y desglose de puntos.
- **Retos Comunitarios:** Seguimiento interactivo del avance en desafíos grupales e individuales de sostenibilidad.
- **Catálogo de Recompensas:** Beneficios e incentivos canjeables mediante ecopuntos acumulados.

---

## 2. Arquitectura de Software

La aplicación implementa una **Arquitectura en Capas (Layered Architecture)** siguiendo las recomendaciones oficiales de Google para Android:

```text
com.retoecotik.app/
├── data/
│   ├── local/
│   │   ├── dao/          # Interfaces DAO de Room
│   │   ├── database/     # Base de datos SQLite Room (AppDatabase)
│   │   └── entity/       # Entidades mapeadas a tablas SQLite
│   └── repository/       # Implementaciones concretas de repositorios
├── domain/
│   ├── model/            # Modelos de datos inmutables y enums
│   ├── repository/       # Contratos abstractos de persistencia
│   └── usecase/          # Lógica de negocio y casos de uso
└── presentation/
    ├── components/       # Componentes visuales reutilizables Material 3
    ├── navigation/       # Grafo de navegación con Navigation Compose
    ├── screens/          # Pantallas de la aplicación (Auth, Home, Reciclaje, Historial, Retos, Perfil)
    └── theme/            # Paleta de colores, tipografía y tema
```

---

## 3. Stack Tecnológico

| Componente | Tecnología | Versión |
| :--- | :--- | :--- |
| **Plataforma** | Android Nativo | API 26 a 35 (`minSdk = 26`, `compileSdk = 35`) |
| **Lenguaje** | Kotlin | 2.0.21 |
| **UI Toolkit** | Jetpack Compose + Material 3 | BOM 2024.10.00 |
| **Persistencia** | Room Database sobre SQLite | 2.6.1 (con KSP 2.0.21-1.0.28) |
| **Navegación** | Navigation Compose | 2.8.3 |
| **Concurrencia** | Kotlin Coroutines & Flow | 1.9.0 |
| **Gestión de Estado** | ViewModel & StateFlow | UDF (Unidirectional Data Flow) |
| **Build System** | Gradle Wrapper | Gradle 8.9 / AGP 8.7.0 |

---

## 4. Instrucciones de Compilación y Ejecución

### Prerrequisitos:
- JDK 17 o JDK 21 instalado.
- Android SDK con plataforma `android-35` o `android-34`.
- Android Studio Ladybug (2024.2.1+) o consola de comandos.

### Compilación desde terminal:
```powershell
# Compilar APK en modo Debug
.\gradlew.bat assembleDebug

# El archivo APK resultante se generará en:
# app/build/outputs/apk/debug/app-debug.apk
```

### Ejecución en Emulador o Dispositivo:
```powershell
# Instalar en el dispositivo/emulador activo
.\gradlew.bat installDebug

# Iniciar la actividad principal mediante ADB
adb shell am start -n com.retoecotik.app.debug/com.retoecotik.app.presentation.MainActivity
```

---

## 5. Licencia y Buenas Prácticas

Proyecto desarrollado aplicando estándares estrictos de codificación, accesibilidad universal (WCAG 2.1 / DUA con zonas táctiles mínimas de 48dp) y principios de desarrollo seguro (*Local-First* con contraseñas hasheadas en SHA-256).
