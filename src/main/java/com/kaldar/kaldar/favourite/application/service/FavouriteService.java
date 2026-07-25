package com.kaldar.kaldar.favourite.application.service;

import com.kaldar.kaldar.favourite.application.dto.response.FavouriteResponse;

import java.util.List;

public interface FavouriteService {

    List<FavouriteResponse> getFavourites(Long customerId);

    void addFavourite(Long customerId, Long dryCleanerId);

    void removeFavourite(Long customerId, Long dryCleanerId);
}
