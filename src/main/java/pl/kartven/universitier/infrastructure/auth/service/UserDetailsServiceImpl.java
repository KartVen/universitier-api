package pl.kartven.universitier.infrastructure.auth.service;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.kartven.universitier.domain.repository.StaffRepository;
import pl.kartven.universitier.domain.repository.StudentRepository;
import pl.kartven.universitier.domain.repository.UserRepository;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final static String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@universitier\\.edu\\.pl$";
    private final static Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        var username = removeEmailDomain(usernameOrEmail);
        var student = Try.of(() -> Long.parseLong(username))
                .map(studentRepository::findByIdWithU)
                .map(Optional::get);
        if(student.isSuccess()) return UserPrincipal.map(student.get().getUser(), student.get().getId().toString());
        var staff = staffRepository.findByKeyIdWithU(username);
        return staff.map(value -> UserPrincipal.map(value.getUser(), value.getKeyId()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + usernameOrEmail));
    }

    public static String removeEmailDomain(String username) {
        Matcher matcher = EMAIL_PATTERN.matcher(username);
        return matcher.find() ? matcher.group().split("@")[0] : username;
    }
}