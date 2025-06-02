package com.laterna.connexemain.v1.message.hidden;

import com.laterna.connexemain.v1.message.Message;
import com.laterna.connexemain.v1.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HiddenMessageService {

    private final HiddenMessageRepository hiddenMessageRepository;

    @Transactional
    public void hide(Message message, User currentUser) {
        HiddenMessage newHiddenMessage = HiddenMessage.builder()
                .userId(currentUser.getId())
                .messageId(message.getId())
                .build();

        hiddenMessageRepository.save(newHiddenMessage);
    }
}
