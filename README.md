# Imposter — Partyspiel

Eine deutsche Pass-and-Play Android-App inspiriert von *Splash – Imposter Game*.

Alle bis auf einen (oder mehrere — du entscheidest!) sehen das gleiche
geheime Wort. Reihum sagt jeder ein einzelnes Hinweiswort dazu. Die Imposter
müssen bluffen. Nach der Hinweisrunde stimmen alle ab, wer der Imposter ist.
Erwischt das Team einen, gewinnt das Team. Sonst gewinnen die Imposter.

## Features

- 3–16 Spieler auf einem Handy (Pass-and-Play)
- 11 deutsche Kategorien: Tiere, Essen, Orte, Gegenstände, Sport, Filme,
  Berufe, Obst, Instrumente, Technik **und „Lustiges"** (generationen­
  übergreifend lustige Wörter, die sowohl 20- als auch 50-Jährige kennen)
- **Frei wählbare Imposter-Anzahl**: 0 bis Anzahl Spieler – fix, zufällig
  oder automatisch skaliert
- **Optionales Zeitlimit** für die Hinweisrunde mit Pause/Reset und
  Farb­warnung in den letzten Sekunden
- **Zufalls-Anzeige am Rundenende**, wer die nächste Runde anfängt
- Karten zum Aufdecken halten (niemand schaut aus Versehen)
- Reihenfolge der Spieler einblendbar
- Laufender Punktestand über alle Runden

## Spielablauf

1. Öffne die App und tippe auf **SPIELEN**.
2. Gib die Namen der Mitspieler ein (3–16).
3. Über **Einstellungen** Kategorien und Imposter-Anzahl wählen
   (Fest / Zufall / Auto), Zeitlimit aktivieren bei Bedarf.
4. **Runde starten**. Reicht das Handy reihum weiter — jeder hält die
   Karte gedrückt und sieht entweder das geheime Wort oder die
   **IMPOSTER**-Karte.
5. Hinweisrunde: jeder sagt EIN Wort zum geheimen Begriff. Imposter bluffen.
6. **Bereit?** → Tippt auf den Button und stimmt ab.
7. Auf dem Ergebnisbildschirm: geheimes Wort, Imposter, Punktestand und ein
   zufällig gewählter Spieler, der die nächste Runde anfängt.

### Punkte

- **Team gewinnt** (Imposter erwischt): jedes Team-Mitglied **+1 Pkt**.
- **Imposter gewinnen** (entkommen): jeder Imposter **+2 Pkt**.
- **Freie Runde** (0 Imposter): alle **+1 Pkt**.
- **Chaos-Runde** (alle Imposter): alle **+2 Pkt**.

## Bauen

```bash
./gradlew :app:assembleDebug
# Ergebnis: app/build/outputs/apk/debug/app-debug.apk
```

Benötigt Android SDK 34 + JDK 17. Pinned: Gradle 8.7, AGP 8.5.2,
Kotlin 1.9.24, Compose BOM 2024.06.

## Lizenz

MIT.
