#Spring Roo Timestamp Add-on

This Spring Roo timestamp add-on allows you to easily add timestamp fields for 'created' and 'updated' to your JPA entities. It provides an @RooTimestamp annotation.

##Quick start to using the timestamp add-on

Download the latest snapshot located in downloads here [com.rcaloras.roo.addon.timestamp-0.1.1-SNAPSHOT.jar](https://github.com/downloads/rcaloras/spring-roo-addon-timestamp/com.rcaloras.roo.addon.timestamp-0.1.1-SNAPSHOT.jar)

From a Roo shell:
```bash
osgi start --url file://$PATH_TO_DOWNLOAD/com.rcaloras.roo.addon.timestamp-0.1.1-SNAPSHOT.jar
timestamp setup
timestamp all
```
###Annotate your entities with @RooTimestamp

```java
@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooTimestamp
public class Location  {
	
	String name;

	String address;

	String city;

	....
}
```
### ITDs are then created for each entity marked
```java
privileged aspect Location_Roo_Timestamp {
    
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date Location.created;
    
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date Location.updated;
    
    @PrePersist
    public void Location.onCreate() {
        this.created=new java.util.Date();
    }
    
    @PreUpdate
    public void Location.onUpdate() {
        this.updated=new java.util.Date();
    }
    
}
```

##Timestamp Add-on Commands
(Displayed with help timestamp from a Roo shell)
'''bash
* timestamp add - Adds @RooTimestamp to target type
* timestamp all - Adds @RooTimestamp to all types annotated with @RooJavaBean
* timestamp remove - Remove all dependencies and removes all annotations of @RooTimestamp
* timestamp setup - Setup Timestamp addon
```


Any issues or questions are more than welcome. More documentaion to come.
