/**
 * Favourite module — allows customers to save and manage their favourite dry cleaners.
 *
 * <p>Architecture: Modulithic slice.</p>
 *
 * <ul>
 *   <li>{@code api} — REST controllers (path: /api/v1/favorites)</li>
 *   <li>{@code application.service} — business logic</li>
 *   <li>{@code domain.model} — JPA entities</li>
 *   <li>{@code domain.repository} — Spring Data repositories</li>
 * </ul>
 *
 * <p>Note: URL path uses American spelling {@code favorites} to match the frontend
 * contract. Java types use British spelling {@code Favourite}.</p>
 */
package com.kaldar.kaldar.favourite;
