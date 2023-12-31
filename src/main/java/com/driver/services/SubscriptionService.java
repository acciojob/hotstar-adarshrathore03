package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            subscription.setTotalAmountPaid((500+(200*subscription.getNoOfScreensSubscribed())));
        }
        else if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            subscription.setTotalAmountPaid(800+(250*subscription.getNoOfScreensSubscribed()));
        }
        else if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            subscription.setTotalAmountPaid(1000+(350*subscription.getNoOfScreensSubscribed()));
        }
        subscription.setUser(user);
        Date date = new Date();
        subscription.setStartSubscriptionDate(date);
        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);

        return subscription.getTotalAmountPaid();

        //return null;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)){
                throw new Exception("Already the best Subscription");
            }
            else if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.PRO)){
                int currentCost = user.getSubscription().getTotalAmountPaid();
                int updatedCost = 1000+(350*user.getSubscription().getNoOfScreensSubscribed());
                user.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
                user.getSubscription().setTotalAmountPaid(updatedCost);

                return updatedCost-currentCost;
            }
            else if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.BASIC)){
                int currentCost = user.getSubscription().getTotalAmountPaid();
                int updatedCost = 800+(250*user.getSubscription().getNoOfScreensSubscribed());
                user.getSubscription().setSubscriptionType(SubscriptionType.PRO);
                user.getSubscription().setTotalAmountPaid(updatedCost);

                return updatedCost-currentCost;
            }
            //checkbyme
            userRepository.save(user);
        }
        return null;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        int cost = 0;
        for(Subscription subscription : subscriptions){
            cost+=subscription.getTotalAmountPaid();
        }
        return cost;
    }

}
