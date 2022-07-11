# NodeDB - Android based NoSQL Database
NodeDB is an Android based NoSQL Database system that provides free, scalable easy and secure Data Management system for Android application. It's fast, easy, and scalable database to store, read, and manage data easily.

## Add NodeDB to your Android app.

### Add JitPack into your buidl.gradle

```
allprojects {

		repositories {

			...

			maven { url 'https://jitpack.io' }

		}

	}
```

### Add NodeDB dependency

```
dependencies {

	        implementation 'com.github.rexllc:node-db:1.0.0-beta'

	}
```

Initialize NodeApp before using and working on it.
```
NodeApp.initialize(this);
```
## Create or Read Database
To start adding data to your database, you need specify it. It will created if not exists.
```
NodeDB db = new NodeDB("name");
```

## Add data to the database
```
HashMap<String, Object> map = new HashMap<>();
map.put("id", db.getKey());
map.put("name", "John");
map.put("surname", "Doe");
map.put("age", "20");
//Then push the data and refresh the table.
db.put(map).push();
```

## Add the listener
Listen for any changes on your database by using `QueryEventListener`
```
db.addQueryEventListener(eventListener);

/*You can also use ValueEventListener to retrieve specific value such user details.*/

db.addValueEventListener(eventListener);

QueryEventListener eventListener;
eventListener = new QueryEventListener() {
     @Override
     public void onQuery(Query query) {
       //For getting single value
       HashMap<String, Object> map = query.getData();
       //For getting all list.
       ArrayList<HashMap<String, Object> list = query.getQuery();
     }
     
     @Override
     public void onError(QueryError error) {}
};
```
## Get single value from the database
To get value from the database with the specific key.
Use `db.get((int)id).addValueEventListener(eventListener);`
it will retrieve all associates records to the id.
Like
```
{
  "key" : "value",
  ...
}
```
## Delete value from the database
You can also remove a specific value in the table.
```
db.child(id).removeValue("value");
```
All ids are integer, and don't use String as key/id.
