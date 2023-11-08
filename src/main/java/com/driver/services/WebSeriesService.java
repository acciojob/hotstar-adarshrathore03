package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        Optional<WebSeries> webSeriesOptional =Optional.ofNullable(webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName()));
        if(webSeriesOptional.isPresent()){
            throw new Exception("Series is already present");
        }
        Optional<ProductionHouse> productionHouseOptional = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId());

        if(productionHouseOptional.isPresent()){
            ProductionHouse productionHouse = productionHouseOptional.get();
            double sum = (productionHouse.getRatings()*productionHouse.getWebSeriesList().size())+webSeriesEntryDto.getRating();
            int size = productionHouse.getWebSeriesList().size()+1;
            double rating = sum/size;
            productionHouse.setRatings(rating);
            WebSeries webSeries = new WebSeries();
            webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
            webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
            webSeries.setRating(webSeriesEntryDto.getRating());
            webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

            productionHouse.getWebSeriesList().add(webSeries);
            webSeries.setProductionHouse(productionHouse);
            webSeriesRepository.save(webSeries);
            return webSeries.getId();
        }
        else{
            return null;
        }


    }

}
