package com.oms.auth.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.oms.auth.dto.UserSignupEvent;

@Service
public class KafkaProduserSerivceImpl implements KafkaProducerService{
	
	private static final Logger log = LoggerFactory.getLogger(KafkaProduserSerivceImpl.class);
	
	@Autowired
	private KafkaTemplate<String, UserSignupEvent> kafkaTemplate;

	@Override
	public void sendUserSignupEvent(UserSignupEvent event) {
		log.info("Invoked into sendUserSignupEvent -->");
		try {
			kafkaTemplate.send("user-signup-topic",event);
		}catch (Exception e) {
			log.error("Exception occured at the KafkaProduserSerivceImpl : "+ e);
			
		}
				
	}

	
}
