package pl.bodzioch.damian.spring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pl.bodzioch.damian.spring.model.TVItem;

import java.util.List;

public interface ItemRepository extends MongoRepository<TVItem, String> {

    @Query("{name: '?0'}")
    TVItem findTVItemByName(String name);

    @Query(value = "{category:  '?0'}", fields = "{'name': 1, 'quantity': 1}")
    List<TVItem> findAll(String category);

    public long count();
}
