package com.laterna.connexeauth.v1.token.refresh;

import com.laterna.connexeauth.v1.token.shared.TokenRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RefreshTokenRepository extends TokenRepository<RefreshToken> {

}
