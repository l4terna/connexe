package com.laterna.connexeauth.v1.token.refresh;


import com.laterna.connexeauth.v1.token.shared.Token;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.laterna.connexeauth.v1.usersession.UserSession;

@SuperBuilder
@DiscriminatorValue("REFRESH")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class RefreshToken extends Token {

    @OneToOne
    @JoinColumn(name = "user_session_id")
    private UserSession userSession;
}
