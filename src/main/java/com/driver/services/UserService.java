package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        Optional<User> userOptional = userRepository.findById(user.getId());
        User u = null;
        if(!userOptional.isPresent()){
            u = userRepository.save(user);
            return u.getId();
        }
        return -1;
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            int cnt = 0;
            User user = userOptional.get();
            int age = user.getAge();
            SubscriptionType subscriptionType = user.getSubscription().getSubscriptionType();
            List<WebSeries> webSeriesList = webSeriesRepository.findAll();

            for(WebSeries webSeries : webSeriesList){
                if(age>=webSeries.getAgeLimit()){
                    if(subscriptionType==SubscriptionType.BASIC && webSeries.getSubscriptionType() == subscriptionType)
                        cnt++;
                    else if(subscriptionType==SubscriptionType.PRO && (webSeries.getSubscriptionType()==SubscriptionType.BASIC || webSeries.getSubscriptionType()==SubscriptionType.PRO))
                        cnt++;
                    else if(subscriptionType==SubscriptionType.ELITE)
                        cnt++;
                }
            }
            return cnt;
        }
        return null;
    }


}
