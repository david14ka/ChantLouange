package com.davidkazad.chantlouange.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps CV song numbers to their streaming audio URLs.
 * Source: https://cantiques.yapper.fr/player/?filter=CV
 */
public class AudioMapper {

    private static final String BASE_URL = "https://cantiques.yapper.fr/media/";
    public static final int CV_BOOK_ID = 2;

    private static final Map<String, String[]> CV_AUDIO_MAP = new HashMap<>();

    static {
        CV_AUDIO_MAP.put("5", new String[]{"hymns/mp3/CV/CV_005-Jesus_Jesus_polyinstru.mp3", "hymns/mp3/CV/CV_005-Jesus_Jesus_mono-piano.mp3"});
        CV_AUDIO_MAP.put("6", new String[]{"hymns/mp3/CV/CV_006-A_Dieu_soit_la_gloire_polyinstru.mp3", "hymns/mp3/CV/CV_006-A_Dieu_soit_la_gloire_mono-piano.mp3"});
        CV_AUDIO_MAP.put("8", new String[]{"hymns/mp3/CV/CV_008-Le_nom_de_Jesus_polyinstru.mp3", "hymns/mp3/CV/CV_008-Le_nom_de_Jesus_mono-piano.mp3"});
        CV_AUDIO_MAP.put("11", new String[]{"hymns/mp3/CV/CV_011-Adorable_mystere_polyinstru.mp3", "hymns/mp3/CV/CV_011-Adorable_mystere_mono-piano.mp3"});
        CV_AUDIO_MAP.put("17", new String[]{"hymns/mp3/CV/CV_017-A_celui_qui_nous_a_laves_polyinstru.mp3", "hymns/mp3/CV/CV_017-A_celui_qui_nous_a_laves_mono-piano.mp3"});
        CV_AUDIO_MAP.put("18", new String[]{"hymns/mp3/CV/CV_018-Vers_toi_monte_notre_hommage_polyinstru.mp3", "hymns/mp3/CV/CV_018-Vers_toi_monte_notre_hommage_mono-piano.mp3"});
        CV_AUDIO_MAP.put("20", new String[]{"hymns/mp3/CV/CV_020-Oh_quel_bonheur_de_le_connaitre_polyinstru.mp3", "hymns/mp3/CV/CV_020-Oh_quel_bonheur_de_le_connaitre_mono-piano.mp3"});
        CV_AUDIO_MAP.put("22", new String[]{"hymns/mp3/CV/CV_022-Je_l_ai_trouve_le_bonheur_ineffable_polyinstru.mp3", "hymns/mp3/CV/CV_022-Je_l_ai_trouve_le_bonheur_ineffable_mono-piano.mp3"});
        CV_AUDIO_MAP.put("24", new String[]{"hymns/mp3/CV/CV_024-Par_tous_les_saints_glorifie_polyinstru.mp3", "hymns/mp3/CV/CV_024-Par_tous_les_saints_glorifie_mono-piano.mp3"});
        CV_AUDIO_MAP.put("26", new String[]{"hymns/mp3/CV/CV_026-Les_rayons_de_l_amour_divin_polyinstru.mp3", "hymns/mp3/CV/CV_026-Les_rayons_de_l_amour_divin_mono-piano.mp3"});
        CV_AUDIO_MAP.put("28", new String[]{"hymns/mp3/CV/CV_028-Oh_que_toute_la_terre_entonne_polyinstru.mp3", "hymns/mp3/CV/CV_028-Oh_que_toute_la_terre_entonne_mono-piano.mp3"});
        CV_AUDIO_MAP.put("29", new String[]{"hymns/mp3/CV/CV_029-Redempteur_adorable_polyinstru.mp3", "hymns/mp3/CV/CV_029-Redempteur_adorable_mono-piano.mp3"});
        CV_AUDIO_MAP.put("30", new String[]{"hymns/mp3/CV/CV_030-Ma_richesse_ma_gloire_polyinstru.mp3", "hymns/mp3/CV/CV_030-Ma_richesse_ma_gloire_mono-piano.mp3"});
        CV_AUDIO_MAP.put("34", new String[]{"hymns/mp3/CV/CV_034-Ah_si_ton_sang_polyinstru.mp3"});
        CV_AUDIO_MAP.put("41", new String[]{"hymns/mp3/CV/CV_041-Roi_couvert_de_blessures_polyinstru.mp3", "hymns/mp3/CV/CV_041-Roi_couvert_de_blessures_mono-piano.mp3"});
        CV_AUDIO_MAP.put("42", new String[]{"hymns/mp3/CV/CV_042-Agneau_de_Dieu_polyinstru.mp3", "hymns/mp3/CV/CV_042-Agneau_de_Dieu_mono-piano.mp3"});
        CV_AUDIO_MAP.put("43", new String[]{"hymns/mp3/CV/CV_043-Jesus_Christ_est_ma_sagesse_polyinstru.mp3", "hymns/mp3/CV/CV_043-Jesus_Christ_est_ma_sagesse_mono-piano.mp3"});
        CV_AUDIO_MAP.put("46", new String[]{"hymns/mp3/CV/CV_046-Viens_mon_ame_polyinstru.mp3", "hymns/mp3/CV/CV_046-Viens_mon_ame_mono-piano.mp3"});
        CV_AUDIO_MAP.put("47", new String[]{"hymns/mp3/CV/CV_047-C_est_toi_Jesus_polyinstru.mp3", "hymns/mp3/CV/CV_047-C_est_toi_Jesus_mono-piano.mp3"});
        CV_AUDIO_MAP.put("57", new String[]{"hymns/mp3/CV/CV_057-Viens_a_la_croix_polyinstru.mp3", "hymns/mp3/CV/CV_057-Viens_a_la_croix_mono-piano.mp3"});
        CV_AUDIO_MAP.put("70", new String[]{"hymns/mp3/CV/CV_070-Coeurs_fatigues_polyinstru.mp3", "hymns/mp3/CV/CV_070-Coeurs_fatigues_mono-piano.mp3"});
        CV_AUDIO_MAP.put("71", new String[]{"hymns/mp3/CV/CV_071-Reviens_polyinstru.mp3", "hymns/mp3/CV/CV_071-Reviens_mono-piano.mp3"});
        CV_AUDIO_MAP.put("74", new String[]{"hymns/mp3/CV/CV_074-Arrete_o_pecheur_arrete_polyinstru.mp3", "hymns/mp3/CV/CV_074-Arrete_o_pecheur_arrete_mono-piano.mp3"});
        CV_AUDIO_MAP.put("75", new String[]{"hymns/mp3/CV/CV_075-Comme_un_phare_sur_la_plage_polyinstru.mp3", "hymns/mp3/CV/CV_075-Comme_un_phare_sur_la_plage_mono-piano.mp3"});
        CV_AUDIO_MAP.put("77", new String[]{"hymns/mp3/CV/CV_077-Reviens_a_ton_Pere_polyinstru.mp3", "hymns/mp3/CV/CV_077-Reviens_a_ton_Pere_mono-piano.mp3"});
        CV_AUDIO_MAP.put("79", new String[]{"hymns/mp3/CV/CV_079-Il_est_un_roc_seculaire_polyinstru.mp3", "hymns/mp3/CV/CV_079-Il_est_un_roc_seculaire_mono-piano.mp3"});
        CV_AUDIO_MAP.put("84", new String[]{"hymns/mp3/CV/CV_084-Nous_voguons_vers_un_beau_rivage_polyinstru.mp3", "hymns/mp3/CV/CV_084-Nous_voguons_vers_un_beau_rivage_mono-piano.mp3"});
        CV_AUDIO_MAP.put("86", new String[]{"hymns/mp3/CV/CV_086-Venez_au_Sauveur_qui_vous_aime_polyinstru.mp3", "hymns/mp3/CV/CV_086-Venez_au_Sauveur_qui_vous_aime_mono-piano.mp3"});
        CV_AUDIO_MAP.put("87", new String[]{"hymns/mp3/CV/CV_087-Cest_encore_temps_polyinstru.mp3", "hymns/mp3/CV/CV_087-Cest_encore_temps_mono-piano.mp3"});
        CV_AUDIO_MAP.put("90", new String[]{"hymns/mp3/CV/CV_090-Viens_ame_perdue_polyinstru.mp3", "hymns/mp3/CV/CV_090-Viens_ame_perdue_mono-piano.mp3"});
        CV_AUDIO_MAP.put("94", new String[]{"hymns/mp3/CV/CV_094-Par_ce_chemin_solitaire_polyinstru.mp3", "hymns/mp3/CV/CV_094-Par_ce_chemin_solitaire_mono-piano.mp3"});
        CV_AUDIO_MAP.put("104", new String[]{"hymns/mp3/CV/CV_104-Bientot_le_Seigneur_va_venir_polyinstru.mp3", "hymns/mp3/CV/CV_104-Bientot_le_Seigneur_va_venir_mono-piano.mp3"});
        CV_AUDIO_MAP.put("105", new String[]{"hymns/mp3/CV/CV_105-Pecheur_je_voudrais_te_guerir_polyinstru.mp3"});
        CV_AUDIO_MAP.put("107", new String[]{"hymns/mp3/CV/CV_107-Une_bonne_nouvelle_polyinstru.mp3", "hymns/mp3/CV/CV_107-Une_bonne_nouvelle_mono-piano.mp3"});
        CV_AUDIO_MAP.put("108", new String[]{"hymns/mp3/CV/CV_108-Dis_tout_a_Jesus_polyinstru.mp3", "hymns/mp3/CV/CV_108-Dis_tout_a_Jesus_mono-piano.mp3"});
        CV_AUDIO_MAP.put("111", new String[]{"hymns/mp3/CV/CV_111-Publiez_bien_haut_polyinstru.mp3", "hymns/mp3/CV/CV_111-Publiez_bien_haut_mono-piano.mp3"});
        CV_AUDIO_MAP.put("112", new String[]{"hymns/mp3/CV/CV_112-Jesus_frappe_a_votre_porte_ouvrez_aujourd_hui_mono-piano.mp3"});
        CV_AUDIO_MAP.put("115", new String[]{"hymns/mp3/CV/CV_115-Ou_cherchez_vous_le_bonheur_polyinstru.mp3", "hymns/mp3/CV/CV_115-Ou_cherchez_vous_le_bonheur_mono-piano.mp3"});
        CV_AUDIO_MAP.put("120", new String[]{"hymns/mp3/CV/CV_120-Jesus_par_ton_sang_precieux_polyinstru.mp3", "hymns/mp3/CV/CV_120-Jesus_par_ton_sang_precieux_mono-piano.mp3"});
        CV_AUDIO_MAP.put("122", new String[]{"hymns/mp3/CV/CV_122-Source_feconde_polyinstru.mp3"});
        CV_AUDIO_MAP.put("126", new String[]{"hymns/mp3/CV/CV_126-Roc_seculaire_polyinstru.mp3", "hymns/mp3/CV/CV_126-Roc_seculaire_mono-piano.mp3"});
        CV_AUDIO_MAP.put("127", new String[]{"hymns/mp3/CV/CV_127-Joie_au_ciel_polyinstru.mp3", "hymns/mp3/CV/CV_127-Joie_au_ciel_mono-piano.mp3"});
        CV_AUDIO_MAP.put("128", new String[]{"hymns/mp3/CV/CV_128-Seigneur_ta_grace_m_appelle_polyinstru.mp3", "hymns/mp3/CV/CV_128-Seigneur_ta_grace_m_appelle_mono-piano.mp3"});
        CV_AUDIO_MAP.put("131", new String[]{"hymns/mp3/CV/CV_131-Misericorde_insondable_polyinstru.mp3", "hymns/mp3/CV/CV_131-Misericorde_insondable_mono-piano.mp3"});
        CV_AUDIO_MAP.put("132", new String[]{"recordings/CV_132-Torrents_d_amour.mp3", "hymns/mp3/CV/CV_132-Torrents_d_amour_polyinstru.mp3", "hymns/mp3/CV/CV_132-Torrents_d_amour_mono-piano.mp3"});
        CV_AUDIO_MAP.put("135", new String[]{"hymns/mp3/CV/CV_135-Veux_tu_briser_polyinstru.mp3", "hymns/mp3/CV/CV_135-Veux_tu_briser_mono-piano.mp3"});
        CV_AUDIO_MAP.put("143", new String[]{"hymns/mp3/CV/CV_143-Comme_une_terre_alteree_polyinstru.mp3", "hymns/mp3/CV/CV_143-Comme_une_terre_alteree_mono-piano.mp3"});
        CV_AUDIO_MAP.put("147a", new String[]{"hymns/mp3/CV/CV_147a-Jesus_ta_sainte_presence_polyinstru.mp3", "hymns/mp3/CV/CV_147a-Jesus_ta_sainte_presence_mono-piano.mp3"});
        CV_AUDIO_MAP.put("148", new String[]{"hymns/mp3/CV/CV_148-Jesus_est_au_milieu_de_nous_polyinstru.mp3", "hymns/mp3/CV/CV_148-Jesus_est_au_milieu_de_nous_mono-piano.mp3"});
        CV_AUDIO_MAP.put("149a", new String[]{"hymns/mp3/CV/CV_149a-Toi_qui_disposes_polyinstru.mp3", "hymns/mp3/CV/CV_149a-Toi_qui_disposes_mono-piano.mp3"});
        CV_AUDIO_MAP.put("161", new String[]{"hymns/mp3/CV/CV_161-Ecoutez_l_appel_du_Berger_polyinstru.mp3", "hymns/mp3/CV/CV_161-Ecoutez_l_appel_du_Berger_mono-piano.mp3"});
        CV_AUDIO_MAP.put("162", new String[]{"hymns/mp3/CV/CV_162-Ah_que_je_ne_sois_pas_polyinstru.mp3", "hymns/mp3/CV/CV_162-Ah_que_je_ne_sois_pas_mono-piano.mp3"});
        CV_AUDIO_MAP.put("166", new String[]{"hymns/mp3/CV/CV_166-Qu_il_fait_bon_a_ton_service_polyinstru.mp3", "hymns/mp3/CV/CV_166-Qu_il_fait_bon_a_ton_service_mono-piano.mp3"});
        CV_AUDIO_MAP.put("167", new String[]{"hymns/mp3/CV/CV_167-C_est_mon_joyeux_service_polyinstru.mp3", "hymns/mp3/CV/CV_167-C_est_mon_joyeux_service_mono-piano.mp3"});
        CV_AUDIO_MAP.put("172a", new String[]{"hymns/mp3/CV/CV_172a-Entre_tes_mains_j_abandonne_polyinstru.mp3", "hymns/mp3/CV/CV_172a-Entre_tes_mains_j_abandonne_mono-piano.mp3"});
        CV_AUDIO_MAP.put("174", new String[]{"hymns/mp3/CV/CV_174-Seul_refuge_de_mon_ame_polyinstru.mp3", "hymns/mp3/CV/CV_174-Seul_refuge_de_mon_ame_mono-piano.mp3"});
        CV_AUDIO_MAP.put("175", new String[]{"hymns/mp3/CV/CV_175-L_amour_de_Jesus-Christ_nous_presse_polyinstru.mp3", "hymns/mp3/CV/CV_175-L_amour_de_Jesus-Christ_nous_presse_mono-piano.mp3"});
        CV_AUDIO_MAP.put("180", new String[]{"hymns/mp3/CV/CV_180-La_voix_du_Seigneur_m_appelle_polyinstru.mp3", "hymns/mp3/CV/CV_180-La_voix_du_Seigneur_m_appelle_mono-piano.mp3"});
        CV_AUDIO_MAP.put("184", new String[]{"hymns/mp3/CV/CV_184-Viens_mon_ame_te_reclame_polyinstru.mp3", "hymns/mp3/CV/CV_184-Viens_mon_ame_te_reclame_mono-piano.mp3"});
        CV_AUDIO_MAP.put("187", new String[]{"hymns/mp3/CV/CV_187-C_est_un_rempart_polyinstru.mp3", "hymns/mp3/CV/CV_187-C_est_un_rempart_mono-piano.mp3"});
        CV_AUDIO_MAP.put("188", new String[]{"hymns/mp3/CV/CV_188-Plus_que_vainqueurs_polyinstru.mp3", "hymns/mp3/CV/CV_188-Plus_que_vainqueurs_mono-piano.mp3"});
        CV_AUDIO_MAP.put("191", new String[]{"hymns/mp3/CV/CV_191-Jusqu_a_ta_venue_polyinstru.mp3", "hymns/mp3/CV/CV_191-Jusqu_a_ta_venue_mono-piano.mp3"});
        CV_AUDIO_MAP.put("193", new String[]{"hymns/mp3/CV/CV_193-Le_signal_de_la_victoire_polyinstru.mp3", "hymns/mp3/CV/CV_193-Le_signal_de_la_victoire_mono-piano.mp3"});
        CV_AUDIO_MAP.put("194", new String[]{"hymns/mp3/CV/CV_194-Sans_attendre_polyinstru.mp3", "hymns/mp3/CV/CV_194-Sans_attendre_mono-piano.mp3"});
        CV_AUDIO_MAP.put("200", new String[]{"hymns/mp3/CV/CV_200-Veille_toujours_polyinstru.mp3", "hymns/mp3/CV/CV_200-Veille_toujours_mono-piano.mp3"});
        CV_AUDIO_MAP.put("201", new String[]{"hymns/mp3/CV/CV_201-A_celui_qui_sera_vainqueur_polyinstru.mp3", "hymns/mp3/CV/CV_201-A_celui_qui_sera_vainqueur_mono-piano.mp3"});
        CV_AUDIO_MAP.put("202", new String[]{"hymns/mp3/CV/CV_202-Vous_qui_gardez_les_murs_polyinstru.mp3", "hymns/mp3/CV/CV_202-Vous_qui_gardez_les_murs_mono-piano.mp3"});
        CV_AUDIO_MAP.put("203", new String[]{"hymns/mp3/CV/CV_203-Sentinelle_vigilante_polyinstru.mp3", "hymns/mp3/CV/CV_203-Sentinelle_vigilante_mono-piano.mp3"});
        CV_AUDIO_MAP.put("204a", new String[]{"hymns/mp3/CV/CV_204a-Debout_sainte_cohorte_polyinstru.mp3", "hymns/mp3/CV/CV_204a-Debout_sainte_cohorte_mono-piano.mp3"});
        CV_AUDIO_MAP.put("205", new String[]{"hymns/mp3/CV/CV_205-Travaillons_et_luttons_polyinstru.mp3", "hymns/mp3/CV/CV_205-Travaillons_et_luttons_mono-piano.mp3"});
        CV_AUDIO_MAP.put("207", new String[]{"hymns/mp3/CV/CV_207-Maitre_entends-tu_la_tempete_polyinstru.mp3", "hymns/mp3/CV/CV_207-Maitre_entends-tu_la_tempete_mono-piano.mp3"});
        CV_AUDIO_MAP.put("208", new String[]{"hymns/mp3/CV/CV_208-Compte_les_bienfaits_de_Dieu_polyinstru.mp3", "hymns/mp3/CV/CV_208-Compte_les_bienfaits_de_Dieu_mono-piano.mp3"});
        CV_AUDIO_MAP.put("210", new String[]{"hymns/mp3/CV/CV_210-Une_nacelle_en_silence_polyinstru.mp3", "hymns/mp3/CV/CV_210-Une_nacelle_en_silence_mono-piano.mp3"});
        CV_AUDIO_MAP.put("211", new String[]{"hymns/mp3/CV/CV_211-Tout_est_bien_polyinstru.mp3", "hymns/mp3/CV/CV_211-Tout_est_bien_mono-piano.mp3"});
        CV_AUDIO_MAP.put("213", new String[]{"hymns/mp3/CV/CV_213-Invoque-moi_polyinstru.mp3", "hymns/mp3/CV/CV_213-Invoque-moi_mono-piano.mp3"});
        CV_AUDIO_MAP.put("215a", new String[]{"hymns/mp3/CV/CV_215a-Oh_quelle_paix_parfaite_polyinstru.mp3", "hymns/mp3/CV/CV_215a-Oh_quelle_paix_parfaite_mono-piano.mp3"});
        CV_AUDIO_MAP.put("219", new String[]{"hymns/mp3/CV/CV_219-Dans_ce_triste_monde_polyinstru.mp3", "hymns/mp3/CV/CV_219-Dans_ce_triste_monde_mono-piano.mp3"});
        CV_AUDIO_MAP.put("221", new String[]{"hymns/mp3/CV/CV_221-Tiens_dans_ta_main_polyinstru.mp3", "hymns/mp3/CV/CV_221-Tiens_dans_ta_main_mono-piano.mp3"});
        CV_AUDIO_MAP.put("222", new String[]{"hymns/mp3/CV/CV_222-Ton_fidele_amour_polyinstru.mp3", "hymns/mp3/CV/CV_222-Ton_fidele_amour_mono-piano.mp3"});
        CV_AUDIO_MAP.put("223", new String[]{"hymns/mp3/CV/CV_223-A_Jesus_je_m_abandonne_polyinstru.mp3", "hymns/mp3/CV/CV_223-A_Jesus_je_m_abandonne_mono-piano.mp3"});
        CV_AUDIO_MAP.put("224", new String[]{"hymns/mp3/CV/CV_224-Comme_un_fleuve_immense_polyinstru.mp3", "hymns/mp3/CV/CV_224-Comme_un_fleuve_immense_mono-piano.mp3"});
        CV_AUDIO_MAP.put("226", new String[]{"hymns/mp3/CV/CV_226-Ne_crains_rien_polyinstru.mp3", "hymns/mp3/CV/CV_226-Ne_crains_rien_mono-piano.mp3"});
        CV_AUDIO_MAP.put("230", new String[]{"hymns/mp3/CV/CV_230-Un_chretien_je_croyait_etre_polyinstru.mp3", "hymns/mp3/CV/CV_230-Un_chretien_je_croyait_etre_mono-piano.mp3"});
        CV_AUDIO_MAP.put("233", new String[]{"recordings/CV_233-Je_ne_sais_pourquoi_dans_sa_grace.mp3", "hymns/mp3/CV/CV_233-Je_sais_polyinstru.mp3", "hymns/mp3/CV/CV_233-Je_sais_mono-piano.mp3"});
        CV_AUDIO_MAP.put("234", new String[]{"hymns/mp3/CV/CV_234-Quel_ami_fidele_et_tendre_polyinstru.mp3", "hymns/mp3/CV/CV_234-Quel_ami_fidele_et_tendre_mono-piano.mp3"});
        CV_AUDIO_MAP.put("236", new String[]{"hymns/mp3/CV/CV_236-O_jour_beni_jour_de_victoire_polyinstru.mp3", "hymns/mp3/CV/CV_236-O_jour_beni_jour_de_victoire_mono-piano.mp3"});
        CV_AUDIO_MAP.put("237", new String[]{"hymns/mp3/CV/CV_237-Aux_jours_d_angoisse_et_de_souffrance_polyinstru.mp3", "hymns/mp3/CV/CV_237-Aux_jours_d_angoisse_et_de_souffrance_mono-piano.mp3"});
        CV_AUDIO_MAP.put("238", new String[]{"hymns/mp3/CV/CV_238-Oui_selon_ta_promesse_polyinstru.mp3", "hymns/mp3/CV/CV_238-Oui_selon_ta_promesse_mono-piano.mp3"});
        CV_AUDIO_MAP.put("240", new String[]{"hymns/mp3/CV/CV_240-Un_seul_pas_a_la_fois_polyinstru.mp3", "hymns/mp3/CV/CV_240-Un_seul_pas_a_la_fois_mono-piano.mp3"});
        CV_AUDIO_MAP.put("241", new String[]{"hymns/mp3/CV/CV_241-Saisis_ma_main_craintive_polyinstru.mp3", "hymns/mp3/CV/CV_241-Saisis_ma_main_craintive_mono-piano.mp3"});
        CV_AUDIO_MAP.put("243", new String[]{"hymns/mp3/CV/CV_243-Quel_repos_polyinstru.mp3", "hymns/mp3/CV/CV_243-Quel_repos_mono-piano.mp3"});
        CV_AUDIO_MAP.put("246", new String[]{"hymns/mp3/CV/CV_246-A_lombre_de_tes_ailes_polyinstru.mp3", "hymns/mp3/CV/CV_246-A_lombre_de_tes_ailes_mono-piano.mp3"});
        CV_AUDIO_MAP.put("249", new String[]{"hymns/mp3/CV/CV_249-J_ai_soif_de_ta_presence_polyinstru.mp3", "hymns/mp3/CV/CV_249-J_ai_soif_de_ta_presence_mono-piano.mp3"});
        CV_AUDIO_MAP.put("251", new String[]{"hymns/mp3/CV/CV_251-Ici_pleurer_et_souffrir_polyinstru.mp3", "hymns/mp3/CV/CV_251-Ici_pleurer_et_souffrir_mono-piano.mp3"});
        CV_AUDIO_MAP.put("252", new String[]{"hymns/mp3/CV/CV_252-Voir_mon_Sauveur_face_a_face_polyinstru.mp3", "hymns/mp3/CV/CV_252-Voir_mon_Sauveur_face_a_face_mono-piano.mp3"});
        CV_AUDIO_MAP.put("253", new String[]{"hymns/mp3/CV/CV_253-La_trompette_a_retenti_polyinstru.mp3", "hymns/mp3/CV/CV_253-La_trompette_a_retenti_mono-piano.mp3"});
        CV_AUDIO_MAP.put("254", new String[]{"hymns/mp3/CV/CV_254-Il_va_venir_le_Seigneur_que_jadore_polyinstru.mp3", "hymns/mp3/CV/CV_254-Il_va_venir_le_Seigneur_que_jadore_mono-piano.mp3"});
        CV_AUDIO_MAP.put("257", new String[]{"hymns/mp3/CV/CV_257-Je_ne_sais_pas_le_jour_polyinstru.mp3", "hymns/mp3/CV/CV_257-Je_ne_sais_pas_le_jour_mono-piano.mp3"});
        CV_AUDIO_MAP.put("261", new String[]{"hymns/mp3/CV/CV_261-Nombreux_comme_le_sable_polyinstru.mp3", "hymns/mp3/CV/CV_261-Nombreux_comme_le_sable_mono-piano.mp3"});
        CV_AUDIO_MAP.put("264", new String[]{"hymns/mp3/CV/CV_264-Avec_allegresse_polyinstru.mp3", "hymns/mp3/CV/CV_264-Avec_allegresse_mono-piano.mp3"});
        CV_AUDIO_MAP.put("265", new String[]{"hymns/mp3/CV/CV_265-Vers_le_ciel_polyinstru.mp3", "hymns/mp3/CV/CV_265-Vers_le_ciel_mono-piano.mp3"});
        CV_AUDIO_MAP.put("268", new String[]{"hymns/mp3/CV/CV_268-Pelerin_sur_cette_terre_polyinstru.mp3", "hymns/mp3/CV/CV_268-Pelerin_sur_cette_terre_mono-piano.mp3"});
        CV_AUDIO_MAP.put("272", new String[]{"recordings/CV_272-Connais_tu_cette_cite.mp3", "hymns/mp3/CV/CV_272-Connais_tu_cette_cite_polyinstru.mp3", "hymns/mp3/CV/CV_272-Connais_tu_cette_cite_mono-piano.mp3"});
        CV_AUDIO_MAP.put("273", new String[]{"hymns/mp3/CV/CV_273-Contempler_mon_Dieu_sur_son_trone_polyinstru.mp3", "hymns/mp3/CV/CV_273-Contempler_mon_Dieu_sur_son_trone_mono-piano.mp3"});
        CV_AUDIO_MAP.put("274", new String[]{"hymns/mp3/CV/CV_274-Au_ciel_est_la_maison_du_Pere_polyinstru.mp3", "hymns/mp3/CV/CV_274-Au_ciel_est_la_maison_du_Pere_mono-piano.mp3"});
        CV_AUDIO_MAP.put("275", new String[]{"hymns/mp3/CV/CV_275-Nous_attendons_le_Sauveur_glorieux_polyinstru.mp3", "hymns/mp3/CV/CV_275-Nous_attendons_le_Sauveur_glorieux_mono-piano.mp3"});
        CV_AUDIO_MAP.put("279", new String[]{"hymns/mp3/CV/CV_279-Beni_soit_le_lien_polyinstru.mp3", "hymns/mp3/CV/CV_279-Beni_soit_le_lien_mono-piano.mp3"});
        CV_AUDIO_MAP.put("286", new String[]{"hymns/mp3/CV/CV_286-Chant_d_adieu_polyinstru.mp3", "hymns/mp3/CV/CV_286-Chant_d_adieu_mono-piano.mp3"});
        CV_AUDIO_MAP.put("288", new String[]{"hymns/mp3/CV/CV_288-Le_Chant_du_Reveil_polyinstru.mp3", "hymns/mp3/CV/CV_288-Le_Chant_du_Reveil_mono-piano.mp3"});
        CV_AUDIO_MAP.put("290", new String[]{"hymns/mp3/CV/CV_290-Christ_est_ma_portion_polyinstru.mp3", "hymns/mp3/CV/CV_290-Christ_est_ma_portion_mono-piano.mp3"});
        CV_AUDIO_MAP.put("292", new String[]{"hymns/mp3/CV/CV_292-Suivez_l_Agneau_polyinstru.mp3", "hymns/mp3/CV/CV_292-Suivez_l_Agneau_mono-piano.mp3"});
        CV_AUDIO_MAP.put("293", new String[]{"hymns/mp3/CV/CV_293-Tout_joyeux_benissons_polyinstru.mp3", "hymns/mp3/CV/CV_293-Tout_joyeux_benissons_mono-piano.mp3"});
        CV_AUDIO_MAP.put("294", new String[]{"hymns/mp3/CV/CV_294-Je_suis_petit_polyinstru.mp3", "hymns/mp3/CV/CV_294-Je_suis_petit_mono-piano.mp3"});
        CV_AUDIO_MAP.put("295", new String[]{"hymns/mp3/CV/CV_295-Nul_enfant_nest_trop_petit_polyinstru.mp3", "hymns/mp3/CV/CV_295-Nul_enfant_nest_trop_petit_mono-piano.mp3"});
        CV_AUDIO_MAP.put("298", new String[]{"hymns/mp3/CV/CV_298-Bientot_Jesus_va_nous_prendre_polyinstru.mp3", "hymns/mp3/CV/CV_298-Bientot_Jesus_va_nous_prendre_mono-piano.mp3"});
        CV_AUDIO_MAP.put("299", new String[]{"hymns/mp3/CV/CV_299-Je_suis_la_lumiere_polyinstru.mp3", "hymns/mp3/CV/CV_299-Je_suis_la_lumiere_mono-piano.mp3"});
        CV_AUDIO_MAP.put("301", new String[]{"hymns/mp3/CV/CV_301-Bon_Sauveur_berger_fidele_polyinstru.mp3", "hymns/mp3/CV/CV_301-Bon_Sauveur_berger_fidele_mono-piano.mp3"});
        CV_AUDIO_MAP.put("302", new String[]{"hymns/mp3/CV/CV_302-Le_Seigneur_m_aime_polyinstru.mp3", "hymns/mp3/CV/CV_302-Le_Seigneur_m_aime_mono-piano.mp3"});
        CV_AUDIO_MAP.put("303", new String[]{"hymns/mp3/CV/CV_303-Sous_le_sang_le_precieux_sang_polyinstru.mp3", "hymns/mp3/CV/CV_303-Sous_le_sang_le_precieux_sang_mono-piano.mp3"});
        CV_AUDIO_MAP.put("304", new String[]{"hymns/mp3/CV/CV_304-Jesus_quitta_le_trone_polyinstru.mp3", "hymns/mp3/CV/CV_304-Jesus_quitta_le_trone_mono-piano.mp3"});
        CV_AUDIO_MAP.put("305", new String[]{"hymns/mp3/CV/CV_305-Rayon_de_soleil_polyinstru.mp3", "hymns/mp3/CV/CV_305-Rayon_de_soleil_mono-piano.mp3"});
        CV_AUDIO_MAP.put("306", new String[]{"hymns/mp3/CV/CV_306-Chaque_jour_de_ma_vie_polyinstru.mp3", "hymns/mp3/CV/CV_306-Chaque_jour_de_ma_vie_mono-piano.mp3"});
        CV_AUDIO_MAP.put("307", new String[]{"hymns/mp3/CV/CV_307-Il_est_un_pays_magnifique_polyinstru.mp3", "hymns/mp3/CV/CV_307-Il_est_un_pays_magnifique_mono-piano.mp3"});
        CV_AUDIO_MAP.put("309", new String[]{"hymns/mp3/CV/CV_309-Jesus_ne_change_pas_polyinstru.mp3", "hymns/mp3/CV/CV_309-Jesus_ne_change_pas_mono-piano.mp3"});
        CV_AUDIO_MAP.put("310", new String[]{"hymns/mp3/CV/CV_310-David_n_avait_rien_que_sa_fronde_polyinstru.mp3", "hymns/mp3/CV/CV_310-David_n_avait_rien_que_sa_fronde_mono-piano.mp3"});
        CV_AUDIO_MAP.put("311", new String[]{"hymns/mp3/CV/CV_311-priere_de_lenfant_a_son_coucher_polyinstru.mp3", "hymns/mp3/CV/CV_311-priere_de_lenfant_a_son_coucher_mono-piano.mp3"});
    }

    public static List<String> getAudioUrls(int bookId, String number) {
        List<String> result = new ArrayList<>();
        if (bookId != CV_BOOK_ID) return result;
        String key = number.trim().replaceAll("\\.\\s*$", "").trim().toLowerCase();
        String[] srcs = CV_AUDIO_MAP.get(key);
        if (srcs != null) {
            for (String src : srcs) result.add(BASE_URL + src);
        }
        return result;
    }

    public static boolean hasAudio(int bookId, String number) {
        return !getAudioUrls(bookId, number).isEmpty();
    }
}
