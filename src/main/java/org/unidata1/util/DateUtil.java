package org.unidata1.util;

import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Component
public class DateUtil {

    private static final ZoneId ҚАЗАҚСТАНАЙМАҒЫ = ZoneId.of("Asia/Almaty");
    private static final DateTimeFormatter КҮНФОРМАТЫ = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter УАҚЫТФОРМАТЫ = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter ТОЛЫҚДАТАФОРМАТЫ = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public LocalDateTime қазіргіУақытАлу() {
        return LocalDateTime.now(ҚАЗАҚСТАНАЙМАҒЫ);
    }

    public LocalDate қазіргіКүнАлу() {
        return LocalDate.now(ҚАЗАҚСТАНАЙМАҒЫ);
    }

    public String күндіФорматтау(LocalDate күн) {
        if (күн == null) {
            return "";
        }
        return күн.format(КҮНФОРМАТЫ);
    }

    public String уақыттыФорматтау(LocalTime уақыт) {
        if (уақыт == null) {
            return "";
        }
        return уақыт.format(УАҚЫТФОРМАТЫ);
    }

    public String толықДатаныФорматтау(LocalDateTime толықДата) {
        if (толықДата == null) {
            return "";
        }
        return толықДата.format(ТОЛЫҚДАТАФОРМАТЫ);
    }

    public LocalDate мәтінненКүнгеТүрлендіру(String күнМәтіні) {
        if (күнМәтіні == null || күнМәтіні.isBlank()) {
            return null;
        }
        return LocalDate.parse(күнМәтіні, КҮНФОРМАТЫ);
    }

    public LocalDateTime мәтінненТолықДатағаТүрлендіру(String толықДатаМәтіні) {
        if (толықДатаМәтіні == null || толықДатаМәтіні.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(толықДатаМәтіні, ТОЛЫҚДАТАФОРМАТЫ);
    }

    public long екіКүнАрасындағыАйырмашылық(LocalDate бірншіКүн, LocalDate екіншіКүн) {
        return ChronoUnit.DAYS.between(бірншіКүн, екіншіКүн);
    }

    public long екіУақытАрасындағыАйырмашылық(LocalDateTime бірншіУақыт, LocalDateTime екіншіУақыт, ChronoUnit өлшемБірлігі) {
        return өлшемБірлігі.between(бірншіУақыт, екіншіУақыт);
    }

    public LocalDate айдыңБірішіКүні(LocalDate күн) {
        return күн.with(TemporalAdjusters.firstDayOfMonth());
    }

    public LocalDate айдыңСоңғыКүні(LocalDate күн) {
        return күн.with(TemporalAdjusters.lastDayOfMonth());
    }

    public LocalDate аптаныңБірішіКүні(LocalDate күн) {
        return күн.with(DayOfWeek.MONDAY);
    }

    public LocalDate аптаныңСоңғыКүні(LocalDate күн) {
        return күн.with(DayOfWeek.SUNDAY);
    }

    public List<LocalDate> екіКүнАралығындағыКүндер(LocalDate бастапқыКүн, LocalDate соңғыКүн) {
        List<LocalDate> күндер = new ArrayList<>();
        LocalDate күнАйнымалы = бастапқыКүн;

        while (!күнАйнымалы.isAfter(соңғыКүн)) {
            күндер.add(күнАйнымалы);
            күнАйнымалы = күнАйнымалы.plusDays(1);
        }

        return күндер;
    }

    public LocalDate күнҚосу(LocalDate күн, int күндерСаны) {
        return күн.plusDays(күндерСаны);
    }

    public LocalDate айҚосу(LocalDate күн, int айларСаны) {
        return күн.plusMonths(айларСаны);
    }

    public LocalDate жылҚосу(LocalDate күн, int жылдарСаны) {
        return күн.plusYears(жылдарСаны);
    }

    public boolean оқуЖылындаМа(LocalDate күн, LocalDate оқуЖылыБасы, LocalDate оқуЖылыСоңы) {
        return !күн.isBefore(оқуЖылыБасы) && !күн.isAfter(оқуЖылыСоңы);
    }
}