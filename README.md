#Spring Roo Timestamp Addon
This Spring Roo timestamp addon allows you to easily add timestamp fields for 'created' and 'updated' to your JPA entities. It provides an @RooTimestamp annotation.

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



