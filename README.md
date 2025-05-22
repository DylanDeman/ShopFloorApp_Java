# 2025-java-gent12
## Disclaimer: This is not an official Delaware product! The use of the Delaware logo is just for layout purposes, because Delaware was the "client"

## Groepsleden:
- Robin Ledoux [robin.ledoux@student.hogent.be](mailto:robin.ledoux@student.hogent.be)
- Milan Dhondt [milan.dhondt@student.hogent.be](mailto:milan.dhondt@student.hogent.be)
- Dogukan Uyanik [dogukan.uyanik@student.hogent.be](mailto:dogukan.uyanik@student.hogent.be)
- Sijad Walipoor [sijad.walipoor@student.hogent.be](mailto:sijad.walipoor@student.hogent.be)
- Dylan De Man [dylan.deman@student.hogent.be](mailto:dylan.deman@student.hogent.be) 


## Persistence.xml example (place at src/main/resources/META-INF/persistence.xml)

```
<?xml version="1.0" encoding="UTF-8"?>

<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence 
             https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    
  <persistence-unit name="shopfloor-app" transaction-type="RESOURCE_LOCAL">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <class>domain.User</class>
    <class>domain.Address</class>
    <class>domain.Site</class>
    <class>domain.Machine</class>
    <class>domain.Report</class>
    <class>domain.Maintenance</class>
    <class>domain.FileInfo</class>
    <class>domain.Notification</class>
    <class>util.MachineStatusConverter</class>
    <class>util.ProductionStatusConverter</class>
    
    <properties>
      <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/{DataBaseName}?serverTimezone=UTC"/>
      <property name="jakarta.persistence.jdbc.user" value="{username}"/> 
      <property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
      <property name="jakarta.persistence.jdbc.password" value="{password}"/>  
      <property name="jakarta.persistence.schema-generation.database.action" value="create"/>
    </properties>
  </persistence-unit>
</persistence>
```

This project uses the seeding from the [JS backend](https://github.com/DylanDeman/ShopFloorApp_JS_BE), so it is important to run the migration in that first. Because of this, use the same database as in the Javascript application.
