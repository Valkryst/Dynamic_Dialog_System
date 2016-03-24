package handler;

import javafx.scene.media.AudioClip;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AudioHandler {
    /** A list containing all audio files that have been loaded. */
    private final ConcurrentLinkedQueue<AudioClip> loadedAudioClips = new ConcurrentLinkedQueue<>();
    // todo JavaDoc
    private double mainVolume = 0.7;

    /**
     * Takes in a set of response parameters and attempts to construct, and play,
     * the specified audio file.
     *
     * If the specified audio file has already been loaded, then it is reused instead
     * of loading a new instance of the file.
     *
     * @param path
     *        The internal JAR-path to the audio clip to play.
     *
     * @param priorityIn
     *        The relative priority of the clip with respect to other clips. This value
     *        is used to determine which clips to remove when the maximum allowed number
     *        of clips is exceeded. The lower the priority, the more likely the
     *        clip is to be stopped and removed from the mixer channel it is occupying.
     *        Valid range is any integer; there are no constraints. The default priority
     *        is zero for all clips until changed. The number of simultaneous sounds
     *        that can be played is implementation- and possibly system-dependent.
     *
     * @param volumeIn
     *        The relative volume level at which the clip is played. Valid range is 0.0
     *        (muted) to 1.0 (full volume). Values are clamped to this range internally
     *        so values outside this range will have no additional effect. Volume is
     *        controlled by attenuation, so values below 1.0 will reduce the sound
     *        level accordingly.
     *
     * @param loopIn
     *        Whether or not the clip will loop and play forever.
     */
    public void handleAudioResponse(final String path, final String priorityIn, final String volumeIn, final String loopIn) {
        if(isAlreadyLoaded(path)) {
            loadedAudioClips.parallelStream()
                            .filter(audioClip -> audioClip.getSource().equals(path))
                            .forEach(AudioClip::play);
        } else {

            final AudioClip audioClip;
            int priority;
            double volume;

            // Set the Priority:
            try {
                priority = Integer.valueOf(priorityIn);
            } catch (final NumberFormatException | NullPointerException e) {
                priority = 0;
            }

            // Set the Volume:
            try {
                volume = Double.valueOf(volumeIn);
                volume *= mainVolume;
            } catch (final NumberFormatException | NullPointerException e) {
                volume = mainVolume;
            }

            // Construct Audio Clip:
            audioClip = new AudioClip(this.getClass().getResource(path).toString());
            audioClip.setPriority(priority);
            audioClip.setVolume(volume);

            // Set the clip to loop if specified:
            if (loopIn != null && Boolean.valueOf(loopIn)) {
                audioClip.setCycleCount(AudioClip.INDEFINITE);
            }

            // Play the Audio Clip:
            audioClip.play();

            loadedAudioClips.add(audioClip);
        }
    }

    /**
     * Checks all currently loaded AudioClips to determine
     * whether or not the specified clip has already been
     * loaded.
     *
     * @param path
     *        The path to an audio file that is to be loaded.
     *
     * @return
     *        Whether or not the specified audio file is already
     *        loaded.
     */
    private boolean isAlreadyLoaded(final String path) {
        return loadedAudioClips.parallelStream()
                               .anyMatch(audioClip -> audioClip.getSource().equals(path));
    }
}
