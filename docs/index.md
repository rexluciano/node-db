## Welcome to GitHub Pages

You can use the [editor on GitHub](https://github.com/rexllc/node-db/edit/master/docs/index.md) to maintain and preview the content for your website in Markdown files.

Whenever you commit to this repository, GitHub Pages will run [Jekyll](https://jekyllrb.com/) to rebuild the pages in your site, from the content in your Markdown files.

### Markdown

Markdown is a lightweight and easy-to-use syntax for styling your writing. It includes conventions for

```markdown
Syntax highlighted code block

# Header 1
## Header 2
### Header 3

- Bulleted
- List

1. Numbered
2. List

**Bold** and _Italic_ and `Code` text

[Link](url) and ![Image](src)
```

For more details see [Basic writing and formatting syntax](https://docs.github.com/en/github/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax).

### Jekyll Themes

Your Pages site will use the layout and styles from the Jekyll theme you have selected in your [repository settings](https://github.com/rexllc/node-db/settings/pages). The name of this theme is saved in the Jekyll `_config.yml` configuration file.

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://docs.github.com/categories/github-pages-basics/) or [contact support](https://support.github.com/contact) and we’ll help you sort it out.
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
