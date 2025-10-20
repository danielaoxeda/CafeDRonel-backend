# Testing con JUnit y Mockito - CafeDRonel Backend

Este proyecto ahora incluye una suite completa de tests unitarios y de integración usando JUnit 5 y Mockito.

## 📋 Configuración de Testing

### Dependencias Incluidas

El proyecto ya incluye las siguientes dependencias de testing en el `pom.xml`:

- **spring-boot-starter-test**: Incluye JUnit 5, Mockito, AssertJ, Hamcrest
- **spring-security-test**: Para testing de seguridad
- **h2**: Base de datos en memoria para testing

### Configuración de Testing

- **Perfil de testing**: `application-test.properties` configurado con H2
- **Base de datos**: H2 en memoria para tests de integración
- **Logging**: Configurado para mostrar información de debugging

## 🧪 Tipos de Tests Implementados

### 1. Tests Unitarios

#### AuthService (`ImpAuthServiceTest.java`)
- ✅ Login con credenciales válidas
- ✅ Login con credenciales inválidas
- ✅ Registro de usuario nuevo
- ✅ Registro de usuario existente
- ✅ Verificación de token válido
- ✅ Verificación de token inválido
- ✅ Manejo de excepciones

#### AuthController (`AuthControllerTest.java`)
- ✅ Endpoint de login
- ✅ Endpoint de registro
- ✅ Endpoint de verificación
- ✅ Manejo de errores HTTP
- ✅ Validación de requests

#### PasswordService (`ImpPasswordServiceTest.java`)
- ✅ Recuperación de contraseña con usuario existente
- ✅ Recuperación con usuario no existente
- ✅ Reset de contraseña con código válido
- ✅ Reset con código inválido
- ✅ Generación de códigos de recuperación

### 2. Tests de Integración

#### AuthController (`AuthControllerIntegrationTest.java`)
- ✅ Flujo completo de registro
- ✅ Flujo completo de login
- ✅ Verificación de tokens
- ✅ Persistencia en base de datos H2
- ✅ Validación de datos

#### PasswordController (`PasswordControllerIntegrationTest.java`)
- ✅ Flujo completo de recuperación de contraseña
- ✅ Flujo completo de reset de contraseña
- ✅ Persistencia de códigos de recuperación
- ✅ Validación de endpoints

## 🚀 Cómo Ejecutar los Tests

### Ejecutar Todos los Tests
```bash
mvn test
```

### Ejecutar Tests Específicos
```bash
# Tests unitarios solamente
mvn test -Dtest="*Test"

# Tests de integración solamente
mvn test -Dtest="*IntegrationTest"

# Test específico
mvn test -Dtest="ImpAuthServiceTest"
```

### Ejecutar con Coverage
```bash
mvn test jacoco:report
```

### Ejecutar en IDE
- **IntelliJ IDEA**: Click derecho en la clase de test → "Run Tests"
- **Eclipse**: Click derecho en la clase de test → "Run As" → "JUnit Test"
- **VS Code**: Usar la extensión de Java Test Runner

## 📊 Estructura de Tests

```
src/test/java/com/cafedronel/cafedronelbackend/
├── services/
│   ├── auth/
│   │   └── ImpAuthServiceTest.java
│   └── password/
│       └── ImpPasswordServiceTest.java
├── controllers/
│   ├── auth/
│   │   ├── AuthControllerTest.java
│   │   └── AuthControllerIntegrationTest.java
│   └── password/
│       └── PasswordControllerIntegrationTest.java
└── resources/
    └── application-test.properties
```

## 🔧 Configuración de Base de Datos para Testing

### H2 Console (Opcional)
Durante los tests, puedes acceder a la consola H2 en:
```
http://localhost:8080/h2-console
```

**Configuración de conexión:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (vacío)

## 📝 Mejores Prácticas Implementadas

### 1. Nomenclatura de Tests
- Nombres descriptivos: `metodo_Condicion_ResultadoEsperado`
- Uso de `@DisplayName` para nombres más legibles

### 2. Estructura AAA (Arrange-Act-Assert)
```java
@Test
void login_ConCredencialesValidas_DeberiaRetornarAuthResponse() {
    // Arrange
    when(authService.login(any())).thenReturn(authResponse);
    
    // Act
    AuthResponse response = authService.login(loginRequest);
    
    // Assert
    assertNotNull(response);
    assertEquals("jwt-token", response.token());
}
```

### 3. Mocking Efectivo
- Mock de dependencias externas
- Verificación de interacciones con `verify()`
- Uso de `@MockBean` para tests de integración

### 4. Datos de Test
- Uso de `@BeforeEach` para setup común
- Datos de test realistas pero simples
- Limpieza de datos entre tests

## 🐛 Debugging de Tests

### Logs de Testing
Los logs están configurados para mostrar:
- Queries SQL ejecutadas
- Información de seguridad
- Errores detallados

### Configuración de Logging
```properties
logging.level.org.springframework.security=DEBUG
logging.level.com.cafedronel.cafedronelbackend=DEBUG
```

## 📈 Métricas de Testing

### Cobertura Recomendada
- **Líneas de código**: > 80%
- **Ramas**: > 70%
- **Métodos**: > 90%
- **Clases**: > 85%

### Comandos para Verificar Cobertura
```bash
# Generar reporte de cobertura
mvn clean test jacoco:report

# Ver reporte
open target/site/jacoco/index.html
```

## 🔄 CI/CD Integration

### GitHub Actions (Ejemplo)
```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run tests
        run: mvn test
```

## 🎯 Próximos Pasos

1. **Agregar más tests** para otros servicios y controladores
2. **Implementar tests de performance** con `@Timed`
3. **Agregar tests de seguridad** más específicos
4. **Configurar reportes de cobertura** en CI/CD
5. **Implementar tests de carga** para endpoints críticos

## 📚 Recursos Adicionales

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [H2 Database](http://www.h2database.com/html/main.html)
