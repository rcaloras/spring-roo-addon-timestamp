#Spring Roo Timestamp Addon

###This Spring Roo timestamp addon allows you to easily add timestamp fields for 'created' and 'updated' to your JPA entities. It provides an @RooTimestamp annotation which can be added as such...

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

