package com.kaldar.kaldar.favourite.api;

import com.kaldar.kaldar.favourite.application.dto.response.FavouriteResponse;
import com.kaldar.kaldar.favourite.application.service.FavouriteService;
import com.kaldar.kaldar.shared.api.response.ApiResponse;
import com.kaldar.kaldar.shared.infrastructure.utility.CurrentUserResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kaldar.kaldar.shared.domain.constants.StatusResponse.*;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavouriteController {

    private final FavouriteService favouriteService;
    private final CurrentUserResolver currentUserResolver;

    public FavouriteController(FavouriteService favouriteService,
                                CurrentUserResolver currentUserResolver) {
        this.favouriteService = favouriteService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FavouriteResponse>>> getFavourites() {
        Long customerId = currentUserResolver.getCurrentUserId();
        List<FavouriteResponse> favourites = favouriteService.getFavourites(customerId);
        ApiResponse<List<FavouriteResponse>> response = ApiResponse.<List<FavouriteResponse>>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message("Favourites retrieved")
                .data(favourites)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{dryCleanerId}")
    public ResponseEntity<ApiResponse<Void>> addFavourite(@PathVariable Long dryCleanerId) {
        Long customerId = currentUserResolver.getCurrentUserId();
        favouriteService.addFavourite(customerId, dryCleanerId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(FAVOURITE_ADDED.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{dryCleanerId}")
    public ResponseEntity<ApiResponse<Void>> removeFavourite(@PathVariable Long dryCleanerId) {
        Long customerId = currentUserResolver.getCurrentUserId();
        favouriteService.removeFavourite(customerId, dryCleanerId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .isSuccess(true)
                .status(HttpStatus.OK.value())
                .message(FAVOURITE_REMOVED.getMessage())
                .build();
        return ResponseEntity.ok(response);
    }
}
