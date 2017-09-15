package com.cnous.esc;

import java.util.UUID;

/**
 * Cette classe permet aux partenaires du projet « carte d’étudiant européenne » de générer par son intermédiaire un numéro unique de carte à partir de leurs logiciels (Système de gestion de carte, développements internes, scripts, etc.).
 * <p>
 * Le format est de 16 octets (128 bits) qui sont conformes à une structure spécifique. Pour rendre les choses confuses, il existe quelques structures différentes, mais la plus commune est la structure définie dans la RFC 4122 - et c'est la structure que nous allons utiliser pour notre guide GUID
 * <p>
 * The variant of GUID we're creating defines the following fields
 * Octet 0-3: time_low The low field of the timestamp
 * Octet 4-5: time_mid The middle field of the timestamp
 * Octet 6-7: time_hi_and_version The high field of the timestamp multiplexed with the version number
 * Octet 8: clock_seq_hi_and_reserved The high field of the clock sequence multiplexed with the variant
 * Octet 9: clock_seq_low The low field of the clock sequence
 * Octet 10-15: node The spatially unique node identifier
 *
 * @author Laurent Cernon www.amj-groupe.com
 * @since 14 septembre 2017
 */
public class UuidFactory {
    // Offset from 15 Oct 1582 to 1 Jan 1970
    private static final long OFFSET_MILLIS = 12219292800000L;

    private static long time = 0, oldSysTime = 0;
    private static int clock, hits = 0;
    private static String node = "";

    /**
     * @param prefixe Il permettra de distinguer les serveurs d’un même établissement
     * @param pic     Participant Identification Code
     * @return a CNOUS unique UUID
     * @since 14 septembre 2017
     */
    public static synchronized String getUuid(Integer prefixe, String pic) throws Exception {

        node = getNode(prefixe, pic);

        if (--hits > 0) ++time;
        else {
            long sysTime = System.currentTimeMillis();
            hits = 10000;

            if (sysTime <= oldSysTime) {
                if (sysTime < oldSysTime) {       // SYSTEM CLOCK WAS SET BACK
                    clock = (++clock & 0x3fff) | 0x8000;
                } else {           // REQUESTING UUIDs TOO FAST FOR SYSTEM CLOCK
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }
                    sysTime = System.currentTimeMillis();
                }
            }

            time = sysTime * 10000L + OFFSET_MILLIS;
            oldSysTime = sysTime;
        }

        int low = (int) time;
        int mid = (int) (time >> 32) & 0xffff;

        // 12 bit hi, set high 4 bits to '0001' for RFC 4122 version 1
        int hi = ((int) (time >> 48) & 0x0fff) | 0x1000;

        return String.format("%08X-%04X-%04X-%04X-%s",
                low, mid, hi, clock, node
        ).toLowerCase();
    }

    /**
     * Cette méthode calcule le node utilisé pour la création du UUID
     * node = Prefixe + PIC
     * Initialise également l'horloge.
     *
     * @param intPrefixe Il permet de distinguer les serveurs d’un même établissement
     * @param pic        Participant Identification Code
     * @return node
     * a CNOUS unique UUID
     * @throws "Invalid Prefixe format"  Si jamais le Préfixe est mal formé.
     * @throws "Invalid PIC format"  Si jamais le PIC est mal formé.
     * @author www.amj-groupe.com
     * @since 14 septembre 2017
     */
    private static String getNode(Integer intPrefixe, String pic) throws Exception {
        String concatID;
        String prefixe;

        prefixe = String.format("%03d", intPrefixe);

        if (!prefixe.matches("[0-9]{3}")) {
            throw new Exception("Invalid Prefixe format");
        }
        if (!pic.matches("[0-9]{9}")) {
            throw new Exception("Invalid PIC format");
        }

        concatID = prefixe.concat(pic);


        // 14 bit clock, set high 2 bits to '0001' for RFC 4122 variant 2
        clock = ((int) (Math.random() * 0x3fff)) | 0x8000;

        return concatID;
    }

    /**
     * Test method
     *
     * @param unused
     */
    public static void main(String[] unused) throws Exception {

        String pic = "999859608";
        Integer prefixe;

        for (prefixe = 0; prefixe < 1000; ++prefixe) {


            System.out.println(UuidFactory.getUuid(prefixe, pic));
        }

        System.out.println(UUID.randomUUID());
    }
}