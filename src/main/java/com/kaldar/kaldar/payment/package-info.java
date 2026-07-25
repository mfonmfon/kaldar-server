/**
 * Payment module — handles external wallet funding via Paystack.
 *
 * <p>Architecture: Modulithic slice.</p>
 *
 * <ul>
 *   <li>{@code api} — REST controllers</li>
 *   <li>{@code application.service} — business logic (initiation, webhook, history)</li>
 *   <li>{@code domain.model} — JPA entities + enums</li>
 *   <li>{@code domain.repository} — Spring Data repositories</li>
 *   <li>{@code infrastructure.payment} — PaystackClient HTTP adapter</li>
 * </ul>
 *
 * <p>Configuration (application.yml):</p>
 * <pre>
 * paystack:
 *   secret-key: ${PAYSTACK_SECRET_KEY:}
 *   base-url: https://api.paystack.co
 * </pre>
 */
package com.kaldar.kaldar.payment;
