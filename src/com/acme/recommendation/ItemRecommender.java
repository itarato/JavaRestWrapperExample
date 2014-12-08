package com.acme.recommendation;

import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.ArrayList;
import java.util.List;

public class ItemRecommender extends RecommenderBase {

    public static ArrayList<Integer> recommend(long uid, int count) throws Exception {
        DataModel dataModel = getDataModel();

        UserSimilarity userSimilarity = new EuclideanDistanceSimilarity(dataModel);
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(3, userSimilarity, dataModel);
        UserBasedRecommender userBasedRecommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, userSimilarity);

        List<RecommendedItem> recommendations = userBasedRecommender.recommend(uid, count);
        ArrayList<Integer> list = new ArrayList<>();
        for (RecommendedItem recommendation : recommendations) {
            list.add((int) recommendation.getItemID());
        }

        return list;
    }

}
