package org.example.datn_website_be.auto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DiscountScheduler {

//    @Autowired
//    private BillByEmployeeService billByEmployeeService;
//    @Autowired
//    private CartDetailService cartDetailService;
    @Scheduled(cron = "0 * * * * *")
    public void checkAndUpdateExpiredDiscounts() {
//        billByEmployeeService.findBillsOlder();
//        cartDetailService.findCartDetailsOlderThanOneDay();
    }
}
