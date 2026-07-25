package com.kaldar.kaldar.favourite.service;

import com.kaldar.kaldar.drycleaner.domain.repository.DryCleanerEntityRepository;
import com.kaldar.kaldar.favourite.application.dto.response.FavouriteResponse;
import com.kaldar.kaldar.favourite.application.service.impl.DefaultFavouriteService;
import com.kaldar.kaldar.favourite.domain.model.FavouriteEntity;
import com.kaldar.kaldar.favourite.domain.repository.FavouriteRepository;
import com.kaldar.kaldar.shared.domain.exceptions.FavouriteAlreadyExistsException;
import com.kaldar.kaldar.shared.domain.exceptions.FavouriteNotFoundException;
import com.kaldar.kaldar.shared.domain.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultFavouriteService Unit Tests")
class DefaultFavouriteServiceTest {

    @Mock private FavouriteRepository favouriteRepository;
    @Mock private DryCleanerEntityRepository dryCleanerEntityRepository;

    private DefaultFavouriteService favouriteService;

    @BeforeEach
    void setUp() {
        favouriteService = new DefaultFavouriteService(favouriteRepository, dryCleanerEntityRepository);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private FavouriteEntity buildFavourite(Long customerId, Long dryCleanerId) {
        FavouriteEntity f = new FavouriteEntity();
        f.setId(1L);
        f.setCustomerId(customerId);
        f.setDryCleanerId(dryCleanerId);
        f.setCreatedAt(LocalDateTime.now());
        return f;
    }

    // =========================================================================
    // getFavourites
    // =========================================================================

    @Nested
    @DisplayName("getFavourites()")
    class GetFavourites {

        @Test
        @DisplayName("should return a list of FavouriteResponse for the customer")
        void shouldReturnFavouritesForCustomer() {
            FavouriteEntity f1 = buildFavourite(5L, 101L);
            FavouriteEntity f2 = buildFavourite(5L, 202L);

            when(favouriteRepository.findByCustomerId(5L)).thenReturn(List.of(f1, f2));

            List<FavouriteResponse> result = favouriteService.getFavourites(5L);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(FavouriteResponse::getDryCleanerId)
                    .containsExactly(101L, 202L);
        }

        @Test
        @DisplayName("should return an empty list when customer has no favourites")
        void shouldReturnEmptyListWhenNoFavourites() {
            when(favouriteRepository.findByCustomerId(5L)).thenReturn(List.of());

            List<FavouriteResponse> result = favouriteService.getFavourites(5L);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // addFavourite
    // =========================================================================

    @Nested
    @DisplayName("addFavourite()")
    class AddFavourite {

        @Test
        @DisplayName("should persist a new FavouriteEntity when all preconditions pass")
        void shouldAddFavouriteSuccessfully() {
            when(dryCleanerEntityRepository.existsById(101L)).thenReturn(true);
            when(favouriteRepository.existsByCustomerIdAndDryCleanerId(5L, 101L)).thenReturn(false);
            when(favouriteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            favouriteService.addFavourite(5L, 101L);

            ArgumentCaptor<FavouriteEntity> captor = ArgumentCaptor.forClass(FavouriteEntity.class);
            verify(favouriteRepository).save(captor.capture());
            assertThat(captor.getValue().getCustomerId()).isEqualTo(5L);
            assertThat(captor.getValue().getDryCleanerId()).isEqualTo(101L);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when dry cleaner does not exist")
        void shouldThrowWhenDryCleanerNotFound() {
            when(dryCleanerEntityRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> favouriteService.addFavourite(5L, 999L))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("999");

            verify(favouriteRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw FavouriteAlreadyExistsException when the entry already exists")
        void shouldThrowWhenFavouriteAlreadyExists() {
            when(dryCleanerEntityRepository.existsById(101L)).thenReturn(true);
            when(favouriteRepository.existsByCustomerIdAndDryCleanerId(5L, 101L)).thenReturn(true);

            assertThatThrownBy(() -> favouriteService.addFavourite(5L, 101L))
                    .isInstanceOf(FavouriteAlreadyExistsException.class);

            verify(favouriteRepository, never()).save(any());
        }
    }

    // =========================================================================
    // removeFavourite
    // =========================================================================

    @Nested
    @DisplayName("removeFavourite()")
    class RemoveFavourite {

        @Test
        @DisplayName("should delete the favourite entry when it exists")
        void shouldRemoveFavouriteSuccessfully() {
            when(favouriteRepository.existsByCustomerIdAndDryCleanerId(5L, 101L)).thenReturn(true);

            favouriteService.removeFavourite(5L, 101L);

            verify(favouriteRepository).deleteByCustomerIdAndDryCleanerId(5L, 101L);
        }

        @Test
        @DisplayName("should throw FavouriteNotFoundException when favourite entry does not exist")
        void shouldThrowWhenFavouriteNotFound() {
            when(favouriteRepository.existsByCustomerIdAndDryCleanerId(5L, 999L)).thenReturn(false);

            assertThatThrownBy(() -> favouriteService.removeFavourite(5L, 999L))
                    .isInstanceOf(FavouriteNotFoundException.class)
                    .hasMessageContaining("999");

            verify(favouriteRepository, never()).deleteByCustomerIdAndDryCleanerId(anyLong(), anyLong());
        }
    }
}
