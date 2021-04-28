package registrationService.reg.user;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import registrationService.reg.registration.token.ConfirmationServiceToken;
import registrationService.reg.registration.token.ConfirmationToken;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

  private final static String USERNAME_NOT_FOUND = " user with email %s nod found";
  private final AppUserRepository appUserRepository;
  private final ConfirmationServiceToken confirmationServiceToken;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    return appUserRepository.findByEmail(email).orElseThrow(
        () -> new UsernameNotFoundException(String.format(USERNAME_NOT_FOUND, email)));
  }

  public String singUpUser(AppUser appUser) {

    boolean userExists = appUserRepository.findByEmail(appUser.getEmail()).isPresent();
    if (userExists) {
      throw new IllegalArgumentException("user already exist");
    }

    String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());

    appUser.setPassword(encodedPassword);
    appUserRepository.save(appUser);

    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken = ConfirmationToken.builder()
        .token(token)
        .createdAt(LocalDateTime.now())
        .expiredAt(LocalDateTime.now().plusMinutes(30))
        .appUser(appUser)
        .build();

    // TODO SEND EMAIL;
    confirmationServiceToken.saveConfirmationToken(confirmationToken);
    return token;
  }

  public void enableAppUser(String email) {

   appUserRepository.enableAppUser(email);
  }
}
