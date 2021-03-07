# BACKEND APPLICACION PARA FINANZAS PERSONALES

Servidor de la applicacion para finanzas personales. Esta applicacion sirve para manejar los gastos
personales, ahorro, inversiones, etc.

## EMPEZANDO
Instalar el siguiente software en su sistema operativo (Windows, Linux, MacOS)
* Maven 3.5+
* Java 8+ (JDK)
* Intellij IDEA o Eclipse + Plugin Spring Tool Suite 4
* MongoDB 4.4.1

### INSTALACION 
1. Clonar el proyecto desde el repositorio de git 
   
Para levantar el ambiente local:

	- git clone <repo> 
	- git checkout development 
	- git pull origin development 
	- mvn clean install -Dmaven.test.skip=true  
	- mvn -Dspring.profiles.active=local spring-boot:run
	
### ALGUNAS FUNCIONALIDADES DEL SISTEMA
* API REST
* ABM de ingresos y/o egresos
* Busquedas por filtros
* ABM para operaciones de ahorro
* Autenticacion con JWT basada en roles
* Registro de usuarios
* Implementacion de Google Recaptcha para validar formularios
* Envio de emails 
* Cambios de contraseñas
* Consultas a APIS publicas
* Consultas personalizadas a la base de datos
* Documentacion de APIS con Swagger
* Manejo de exepciones personalizadas
* CORS
* Test unitarios con JUnit
* Envio de SMS con el API de TWILIO (Cuenta de prueba)

#### USO DE APIS EXTERNAS
Para la consulta del valor de U$D se utilizo la API public compartida por [Ramiro Castrogiovanni](https://github.com/Castrogiovanni20)
En el siguiente link esta el repositorio de github de la API [API-DOLAR-ARGENTINA](https://github.com/Castrogiovanni20/api-dolar-argentina)
