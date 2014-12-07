package com.acme.resource;

import com.acme.recommendation.ItemRecommender;
import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("recommendation/{uid}/{count}")
public class Recommendation {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt(@PathParam("uid") String uid, @PathParam("count") String count) throws Exception {
        ArrayList<Integer> nids = ItemRecommender.recommend(Long.valueOf(uid), Integer.valueOf(count));
        Gson gson = new Gson();
        return gson.toJson(nids);
    }

}
