package com.acme.resource;

import com.acme.recommendation.SimilarityRecommender;
import com.google.gson.Gson;
import org.apache.mahout.cf.taste.common.TasteException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Root resource (exposed at "recommendation" path)
 */
@Path("similarity/{nid}/{count}")
public class Similarity {

    /**
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt(@PathParam("nid") String nid, @PathParam("count") String count) throws SQLException, TasteException {
        ArrayList<Integer> nids = SimilarityRecommender.recommend(Long.parseLong(nid), Integer.valueOf(count));
        Gson gson = new Gson();
        return gson.toJson(nids);
    }

}
