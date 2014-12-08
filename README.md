Wrapping Java into a "REST container"
=====================================

This is a highly simplified example on how to wrap a Java library and enable its functionality through REST services. This sample does not intend to present an optimal structure neither efficient code.

In this case we use Mahout (http://mahout.apache.org/) to provide recommendation to a Drupal (actually it does not matter) site - where users can rate content and would like to know:
- regarding to my history (items I voted on) what new items I may like
- having selected an item I would like to see similar items based on the vote of all users


The Java project
----------------

Managing the Java project it's highly recommended to use the best IDEs available: IntelliJ IDEA (https://www.jetbrains.com/idea/) or Eclipse (https://eclipse.org/home/index.php) in order to support builds and archiving.

The project, dependencies, build strategies and such are handled through Maven (http://maven.apache.org/). Maven needs a project description file, called ```pom.xml```.


Deployment
----------
 
As an example - Heroku (https://heroku.com) is an excellent place to deploy and test your Java application. The guide helps you to prepare your app and put on the cloud. Scaling and attaching services is simple through their command line tool. They are also free to try, highly recommended to play with.


Packaging
---------

One easy way to deliver the project to a server is in a JAR file. You can define arguments and execute the package on most platforms. This makes the deployment easy on custom environments.
Packaging into a JAR file you need to create a ```MANIFEST.MF``` file to tell where is the main class, and run the jar command:

```bash
jar -cvfm bundle.jar PATH/TO/MANIFEST.FM PATH/TO/CLASS/FILES
```

Or you can just use the packaging tool inside the IDE - eg build an artifact in IntelliJ IDEA.
 

Packages for the demo
=====================

- Web server - Project Grizzly (https://grizzly.java.net/)
- REST server - Jersey (https://jersey.java.net/)
- JSON parser - GSON (https://code.google.com/p/google-gson/)
- Database driver - MySQL (http://dev.mysql.com/downloads/connector/j/)
- Machine learning engine - Mahout (http://mahout.apache.org/)


Demo explained
--------------

First we tell Jersey where are the resources - which will be discovered through reflection and annotations:

```java
import org.glassfish.jersey.server.ResourceConfig;

ResourceConfig rc = new ResourceConfig().packages("com.acme.resource");
```

Then we create the web server:

```java
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
```

A resource can be defined by a few annotation and the handler method:

```java
package com.acme.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("recommendation/{uid}/{count}")
public class Recommendation {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt(@PathParam("uid") String uid, @PathParam("count") String count) throws Exception {
        Gson gson = new Gson();
        return gson.toJson(SOME_OBJECT);
    }

}
```

You can see we defined the response handler for the GET request for path: recommendation/*/* and we return a JSON string.

On that note, we shall mention the creation of JSON strings, eg:

```java
import com.google.gson.Gson;

ArrayList<Integer> nids = new ArrayList<>();
nids.add(1);
nids.add(2);
// ...
Gson gson = new Gson();
gson.toJson(nids); // -> is JSON string, simples.
```

For the recommendation engine first we need to connect our datasource (Drupal's MySQL database) to the recommender engine:

```java
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.ReloadFromJDBCDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;

MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
dataSource.setURL("jdbc:mysql://localhost/drupal7?user=USER&password=PASSWORD");

JDBCDataModel dm = new MySQLJDBCDataModel(dataSource, "rec_vote", "uid", "nid", "vote", null);
DataModel reloadFromJDBCDataModel = new ReloadFromJDBCDataModel(dm);
```

Here we chose the ReloadFromJDBCDataModel in order to have a better performance over JDBCDataModel which does individual queries instead of storing everything.

Then we can extract the results - for example recommended items:

```java
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

UserSimilarity userSimilarity = new EuclideanDistanceSimilarity(dataModel);
UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(3, userSimilarity, dataModel);
UserBasedRecommender userBasedRecommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, userSimilarity);

List<RecommendedItem> recommendations = userBasedRecommender.recommend(uid, count);
ArrayList<Integer> list = new ArrayList<>();
for (RecommendedItem recommendation : recommendations) {
    list.add((int) recommendation.getItemID());
}
```

Which can be loaded from the REST request handler and pushed to the response.
