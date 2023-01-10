package com.mk.customer;

//import com.mk.amqp.RabbitMQMessageProducer;
//import com.mk.clients.fraud.FraudCheckResponse;
//import com.mk.clients.fraud.FraudClient;
//import com.mk.clients.notification.NotificationClient;
//import com.mk.clients.notification.NotificationRequest;

import com.mk.clients.fraud.FraudCheckResponse;
import com.mk.clients.fraud.FraudClient;
import com.mk.clients.notification.NotificationClient;
import com.mk.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final NotificationClient notificationClient;
//    private final RestTemplate restTemplate;

    private final FraudClient fraudClient;
//    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder().firstName(request.firstName()).lastName(request.lastName()).email(request.email()).build();

//        customerRepository.save(customer);
        customerRepository.saveAndFlush(customer);


//        FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
//                "http://FRAUD/api/v1/fraud-check/{customerId}",
//                FraudCheckResponse.class,
//                customer.getId()
//        );

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());
//
        assert fraudCheckResponse != null;
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to MK...", customer.getFirstName())
        );

        notificationClient.sendNotification(notificationRequest);

//        rabbitMQMessageProducer.publish(
//                notificationRequest,
//                "internal.exchange",
//                "internal.notification.routing-key"
//        );


    }
}
