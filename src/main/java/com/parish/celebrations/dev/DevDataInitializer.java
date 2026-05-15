package com.parish.celebrations.dev;

import com.parish.celebrations.location.domain.Location;
import com.parish.celebrations.location.domain.LocationRepository;
import com.parish.celebrations.person.domain.Person;
import com.parish.celebrations.person.domain.PersonRepository;
import com.parish.celebrations.person.domain.PersonRole;
import com.parish.celebrations.records.domain.*;
import com.parish.celebrations.scheduling.domain.*;
import com.parish.celebrations.user.domain.AppUser;
import com.parish.celebrations.user.domain.UserRepository;
import com.parish.celebrations.user.domain.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    private final UserRepository userRepo;
    private final PersonRepository personRepo;
    private final LocationRepository locationRepo;
    private final CelebrationRepository celebrationRepo;
    private final RecordRepository recordRepo;
    private final PasswordEncoder encoder;

    public DevDataInitializer(UserRepository userRepo, PersonRepository personRepo,
                              LocationRepository locationRepo, CelebrationRepository celebrationRepo,
                              RecordRepository recordRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.personRepo = personRepo;
        this.locationRepo = locationRepo;
        this.celebrationRepo = celebrationRepo;
        this.recordRepo = recordRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) {
            log.info("Dev data already present, skipping initialization.");
            return;
        }
        log.info("Seeding dev data...");

        // Users
        userRepo.save(new AppUser("admin@parish.dev", encoder.encode("admin"), UserRole.ADMIN));
        userRepo.save(new AppUser("secretary@parish.dev", encoder.encode("secretary"), UserRole.SECRETARY));
        userRepo.save(new AppUser("priest@parish.dev", encoder.encode("priest"), UserRole.PRIEST));

        // Location
        Location church = locationRepo.save(new Location("Iglesia de San Marcos", "Calle Mayor 12, 28001 Madrid", 350));

        // Persons — priests
        Person priest1 = personRepo.save(new Person("Juan", "García Ruiz", PersonRole.PRIEST));
        Person priest2 = personRepo.save(new Person("Carlos", "López Martínez", PersonRole.PRIEST));
        Person bishop  = personRepo.save(new Person("Manuel", "Torres Vidal", PersonRole.BISHOP));

        // Persons — faithful
        Person p1  = personRepo.save(new Person("María", "Fernández López", PersonRole.FAITHFUL));
        Person p2  = personRepo.save(new Person("José", "Rodríguez García", PersonRole.FAITHFUL));
        Person p3  = personRepo.save(new Person("Ana", "Martínez Pérez", PersonRole.FAITHFUL));
        Person p4  = personRepo.save(new Person("Pedro", "Sánchez Ruiz", PersonRole.FAITHFUL));
        Person p5  = personRepo.save(new Person("Carmen", "Díaz Torres", PersonRole.FAITHFUL));
        Person p6  = personRepo.save(new Person("Luis", "Moreno García", PersonRole.FAITHFUL));
        Person p7  = personRepo.save(new Person("Isabel", "Jiménez Vega", PersonRole.FAITHFUL));
        Person p8  = personRepo.save(new Person("Antonio", "Álvarez Mora", PersonRole.FAITHFUL));
        Person p9  = personRepo.save(new Person("Elena", "Romero Castro", PersonRole.FAITHFUL));
        Person p10 = personRepo.save(new Person("Miguel", "Serrano Blanco", PersonRole.FAITHFUL));
        Person p11 = personRepo.save(new Person("Lucía", "Navarro Gil", PersonRole.FAITHFUL));
        Person p12 = personRepo.save(new Person("Diego", "Molina Ruiz", PersonRole.FAITHFUL));
        Person p13 = personRepo.save(new Person("Sofía", "Ortiz Campos", PersonRole.FAITHFUL));
        Person p14 = personRepo.save(new Person("Pablo", "Guerrero Ramos", PersonRole.FAITHFUL));
        Person p15 = personRepo.save(new Person("Marta", "Rubio Santos", PersonRole.FAITHFUL));

        UUID locId = church.getId();
        UUID pId1 = priest1.getId();
        UUID pId2 = priest2.getId();
        UUID bpId = bishop.getId();

        LocalDateTime now = LocalDateTime.now();

        // --- COMPLETED celebrations (past) ---

        // 1. Mass (COMPLETED)
        Mass mass1 = new Mass(locId, pId1, now.minusMonths(3), "Por la familia Rodríguez", false);
        mass1.confirm(); mass1.complete();
        celebrationRepo.save(mass1);

        // 2. Baptism (COMPLETED)
        Baptism bap1 = new Baptism(locId, pId1, now.minusMonths(2),
                p11.getId(), List.of(p1.getId(), p2.getId()), List.of(p3.getId(), p4.getId()));
        bap1.confirm(); bap1.complete();
        celebrationRepo.save(bap1);
        recordRepo.save(new BaptismRecord(bap1.getId(), p11.getId(), pId1));

        // 3. Wedding (COMPLETED)
        Wedding wed1 = new Wedding(locId, pId2, now.minusMonths(1).minusDays(5),
                p5.getId(), p6.getId(), List.of(p7.getId(), p8.getId()));
        wed1.markDocumentationComplete();
        wed1.confirm(); wed1.complete();
        celebrationRepo.save(wed1);
        recordRepo.save(new MarriageRecord(wed1.getId(), p5.getId(), p6.getId()));

        // 4. Funeral (COMPLETED)
        Funeral fun1 = new Funeral(locId, pId1, now.minusWeeks(3), p9.getId(), "Cementerio Municipal de Madrid");
        fun1.confirm(); fun1.complete();
        celebrationRepo.save(fun1);
        recordRepo.save(new FuneralRecord(fun1.getId(), p9.getId()));

        // 5. Confirmation (COMPLETED)
        Confirmation conf1 = new Confirmation(locId, pId2, now.minusWeeks(6),
                List.of(p10.getId(), p12.getId(), p13.getId()), bpId);
        conf1.confirm(); conf1.complete();
        celebrationRepo.save(conf1);
        recordRepo.save(new ConfirmationRecord(conf1.getId(), bpId, List.of(p10.getId(), p12.getId(), p13.getId())));

        // --- CONFIRMED celebrations (future) ---

        // 6. Mass (CONFIRMED)
        Mass mass2 = new Mass(locId, pId1, now.plusDays(2), "Por la familia Rodríguez en su aniversario de bodas", true);
        mass2.confirm();
        celebrationRepo.save(mass2);

        // 7. Baptism (CONFIRMED)
        Baptism bap2 = new Baptism(locId, pId1, now.plusDays(12),
                p14.getId(), List.of(p1.getId(), p2.getId()), List.of(p3.getId()));
        bap2.confirm();
        celebrationRepo.save(bap2);

        // 8. Wedding (CONFIRMED)
        Wedding wed2 = new Wedding(locId, pId2, now.plusDays(40),
                p15.getId(), p6.getId(), List.of(p7.getId(), p8.getId()));
        wed2.markDocumentationComplete();
        wed2.confirm();
        celebrationRepo.save(wed2);

        // --- DRAFT celebrations ---

        // 9. First Communion (DRAFT)
        FirstCommunion fc1 = new FirstCommunion(locId, pId1, now.plusMonths(2),
                List.of(p10.getId(), p12.getId()));
        celebrationRepo.save(fc1);

        // 10. Funeral (DRAFT)
        Funeral fun2 = new Funeral(locId, pId2, now.plusDays(7), p13.getId(), null);
        celebrationRepo.save(fun2);

        log.info("Dev data seeded: 3 users, {} persons, 1 location, 10 celebrations, 5 records.",
                personRepo.count() - 3);
    }
}
