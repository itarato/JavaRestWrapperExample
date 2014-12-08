package com.acme.recommendation;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import java.util.ArrayList;
import java.util.List;

public class SimilarityRecommender extends RecommenderBase {

    public static ArrayList<Integer> recommend(long nid, int count) throws TasteException {
        DataModel dataModel = getDataModel();

        ItemSimilarity itemSimilarity = new LogLikelihoodSimilarity(dataModel);
        GenericItemBasedRecommender genericItemBasedRecommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);

        List<RecommendedItem> recommendations = genericItemBasedRecommender.mostSimilarItems(nid, count);
        ArrayList<Integer>list = new ArrayList<>();
        for (RecommendedItem recommendation : recommendations) {
            list.add((int) recommendation.getItemID());
        }

        return list;
    }

}
