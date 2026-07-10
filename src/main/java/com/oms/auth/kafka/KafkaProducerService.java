package com.oms.auth.kafka;

import com.oms.auth.dto.UserSignupEvent;

public interface KafkaProducerService {

	void sendUserSignupEvent(UserSignupEvent event);
}
