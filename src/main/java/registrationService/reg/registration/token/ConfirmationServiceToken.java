package registrationService.reg.registration.token;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfirmationServiceToken {

  private final ConfirmationTokenRepository repository;

  public void saveConfirmationToken(ConfirmationToken token) {

    repository.save(token);
  }

  public Optional<ConfirmationToken> getToken(String token) {

    return repository.findByToken(token);
  }
}
