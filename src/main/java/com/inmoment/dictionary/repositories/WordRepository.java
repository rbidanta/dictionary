package com.inmoment.dictionary.repositories;

import com.inmoment.dictionary.model.Word;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "word", path = "word")
public interface WordRepository extends PagingAndSortingRepository<Word,Long> {
    Word findByWord(String word);
}
