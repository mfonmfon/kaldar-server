package com.kaldar.kaldar.favourite.application.service.impl;

import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.favourite.application.dto.response.FavouriteResponse;
import com.kaldar.kaldar.favourite.application.service.FavouriteService;
import com.kaldar.kaldar.favourite.domain.model.FavouriteEntity;
import com.kaldar.kaldar.favourite.domain.repository.FavouriteRepository;
import com.kaldar.kaldar.shared.domain.exceptions.FavouriteAlreadyExistsException;
import com.kaldar.kaldar.shared.domain.exceptions.FavouriteNotFoundException;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultFavouriteService implements FavouriteService {

    private final FavouriteRepository favouriteRepository;
    private final DryCleanerEntityRepository dryCleanerEntityRepository;

    public DefaultFavouriteService(FavouriteRepository favouriteRepository,
                                   DryCleanerEntityRepository dryCleanerEntityRepository) {
        this.favouriteRepository = favouriteRepository;
        this.dryCleanerEntityRepository = dryCleanerEntityRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavouriteResponse> getFavourites(Long customerId) {
        return favouriteRepository.findByCustomerId(customerId).stream()
                .map(f -> new FavouriteResponse(f.getDryCleanerId(), f.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public void addFavourite(Long customerId, Long dryCleanerId) {
        if (!dryCleanerEntityRepository.existsById(dryCleanerId)) {
            throw new UserNotFoundException("Dry cleaner not found with id: " + dryCleanerId);
        }
        if (favouriteRepository.existsByCustomerIdAndDryCleanerId(customerId, dryCleanerId)) {
            throw new FavouriteAlreadyExistsException(
                    "Dry cleaner is already in your favourites");
        }
        FavouriteEntity favourite = new FavouriteEntity();
        favourite.setCustomerId(customerId);
        favourite.setDryCleanerId(dryCleanerId);
        favouriteRepository.save(favourite);
    }

    @Override
    @Transactional
    public void removeFavourite(Long customerId, Long dryCleanerId) {
        if (!favouriteRepository.existsByCustomerIdAndDryCleanerId(customerId, dryCleanerId)) {
            throw new FavouriteNotFoundException("Favourite not found for dry cleaner id: " + dryCleanerId);
        }
        favouriteRepository.deleteByCustomerIdAndDryCleanerId(customerId, dryCleanerId);
    }
}
