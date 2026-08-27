package com.ecommerce.plateform.orderservice.persistence.mongo.service;

import com.ecommerce.plateform.orderservice.persistence.mongo.entity.DatabaseSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final MongoTemplate mongoTemplate;

    public long generateSequence(String sequenceName) {

        Query query = new Query(Criteria.where("_id").is(sequenceName));

        Update update = new Update().inc("sequence", 1);

        FindAndModifyOptions options = new FindAndModifyOptions()
                .returnNew(true)
                .upsert(true);

        DatabaseSequence sequence = mongoTemplate.findAndModify(query, update, options, DatabaseSequence.class);

        return sequence.getSequence();

    }
}
