package com.ecommerce.plateform.notificationservice.persistance.mongo.service;

import com.ecommerce.plateform.notificationservice.persistance.mongo.entity.DatabaseSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    public long generateSequence(String sequenceName) {

        Query query = new Query(
                Criteria.where("_id").is(sequenceName)
        );

        Update update = new Update().inc("sequence", 1);

        FindAndModifyOptions options = new FindAndModifyOptions()
                .returnNew(true)
                .upsert(true);

        DatabaseSequence counter = mongoOperations.findAndModify(
                query,
                update,
                options,
                DatabaseSequence.class
        );

        return counter != null ? counter.getSequence() : 1;
    }
}