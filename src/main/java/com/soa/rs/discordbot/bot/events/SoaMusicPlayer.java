package com.soa.rs.discordbot.bot.events;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.dv8tion.d4j.player.MusicPlayer;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.AudioTimestamp;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.audio.impl.DefaultProvider;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * The SoaMusicPlayer is a slightly modified version of the implementation
 * example of the JDA-Player library which can be found on Github:
 * https://github.com/DV8FromTheWorld/JDA-Player
 */
public class SoaMusicPlayer {

	private IAudioManager manager;
	private MusicPlayer player;
	private IMessage msg;
	private static final float DEFAULT_VOLUME = 0.35f;
	private boolean inChannel = false;

	/**
	 * Constructor
	 * 
	 * @param msg
	 *            Message from the event
	 */
	public SoaMusicPlayer(IMessage msg) {
		manager = msg.getGuild().getAudioManager();
		if (manager.getAudioProvider() instanceof DefaultProvider) {
			player = new MusicPlayer();
			player.setVolume(DEFAULT_VOLUME);
			manager.setAudioProvider(player);
		} else {
			player = (MusicPlayer) manager.getAudioProvider();
		}
	}

	/**
	 * Sets the message
	 * 
	 * @param msg
	 *            Message from the event
	 */
	public void setMsg(IMessage msg) {
		this.msg = msg;
	}

	/**
	 * Checks to determine if the person who has issued a music command is
	 * permitted to run the MusicPlayer
	 * 
	 * @param event
	 *            MessageReceivedEvent
	 * @return true if user is allowed to issue these commands, false otherwise
	 */
	private boolean checkMusicRoles(MessageReceivedEvent event) {
		IGuild guild = event.getMessage().getGuild();
		List<IRole> roleListing = new LinkedList<IRole>(event.getMessage().getAuthor().getRolesForGuild(guild));
		Iterator<IRole> roleIterator = roleListing.iterator();

		while (roleIterator.hasNext()) {
			IRole role = roleIterator.next();
			if (role.getName().equalsIgnoreCase("Eldar") || role.getName().equalsIgnoreCase("Lian")
					|| role.getName().equalsIgnoreCase("DJ"))
				return true;
		}
		return false;
	}

	/**
	 * Handles the arguments sent to the MusicPlayer.
	 * 
	 * @param event
	 *            MessageReceivedEvent
	 * @param args
	 *            The additional arguments other than ".music" sent to the bot.
	 * @throws MissingPermissionsException
	 *             Thrown if the bot doesn't have permission to take the desired
	 *             action
	 * @throws RateLimitException
	 *             Thrown if the bot has reached the rate limit
	 * @throws DiscordException
	 *             Thrown if any other generic Discord error has occurred.
	 */
	public void handleMusicArgs(MessageReceivedEvent event, String[] args)
			throws MissingPermissionsException, RateLimitException, DiscordException {

		if (!checkMusicRoles(event)) {
			msg.getChannel().sendMessage("Sorry, only Lian+ or DJ rank can run the music player");
			return;
		}

		String message = args[1];
		if (message.equals("volume")) {
			handleVolumeChange(args);
		}

		if (message.equals("list")) {
			handleListMusicQueue();
		}

		if (message.equals("nowplaying")) {
			handleNowPlayingListing();
		}

		if (message.equals("join")) {
			handleJoinChannel();
		}
		if (message.equals("leave")) {
			handleLeaveChannel();
		}

		if (message.equals("skip")) {
			handleSkip();
		}

		if (message.equals("reset")) {
			handleReset();
		}

		if (message.equals("play")) {
			handlePlay(event, args);
		}

		if (message.equals("pause")) {
			handlePause();
		}

		if (message.equals("stop")) {
			handleStop();
		}

		if (message.equals("restart")) {
			handleRestartTrack();
		}
	}

	/**
	 * Handles the stop command
	 * 
	 * @throws MissingPermissionsException
	 * @throws RateLimitException
	 * @throws DiscordException
	 */
	private void handleStop() throws MissingPermissionsException, RateLimitException, DiscordException {
		player.stop();
		msg.getChannel().sendMessage("Playback has been completely stopped.");
	}

	/**
	 * Handles the pause command
	 * 
	 * @throws MissingPermissionsException
	 * @throws RateLimitException
	 * @throws DiscordException
	 */
	private void handlePause() throws MissingPermissionsException, RateLimitException, DiscordException {
		player.pause();
		msg.getChannel().sendMessage("Playback has been paused.");
	}

	/**
	 * Handles the restart command
	 * 
	 * @throws MissingPermissionsException
	 * @throws RateLimitException
	 * @throws DiscordException
	 */
	private void handleRestartTrack() throws MissingPermissionsException, RateLimitException, DiscordException {
		if (player.isStopped()) {
			if (player.getPreviousAudioSource() != null) {
				player.reload(true);
				msg.getChannel().sendMessage("The previous song has been restarted.");
			} else {
				msg.getChannel().sendMessage("The player has never played a song, so it cannot restart a song.");
			}
		} else {
			player.reload(true);
			msg.getChannel().sendMessage("The currently playing song has been restarted!");
		}
	}

	/**
	 * Handles clearing and resetting all of the player's audio states to their
	 * defaults
	 * 
	 * @throws MissingPermissionsException
	 * @throws RateLimitException
	 * @throws DiscordException
	 */
	private void handleReset() throws MissingPermissionsException, RateLimitException, DiscordException {
		player.stop();
		player = new MusicPlayer();
		player.setVolume(DEFAULT_VOLUME);
		manager.setAudioProvider(player);
		msg.getChannel().sendMessage("Music player has been completely reset.");
	}

	/**
	 * Handles requests for the bot to play audio
	 * 
	 * @param event
	 *            MessageReceivedEvent
	 * @param args
	 *            Additional arguments passed to the play event
	 * @throws MissingPermissionsException
	 * @throws RateLimitException
	 * @throws DiscordException
	 */
	private void handlePlay(MessageReceivedEvent event, String[] args)
			throws MissingPermissionsException, RateLimitException, DiscordException {
		// If no URL was provided.
		if (args[1].equals("play") && args.length == 2) {
			if (player.isPlaying()) {
				msg.getChannel().sendMessage("Player is already playing!");
				return;
			} else if (player.isPaused()) {
				player.play();
				msg.getChannel().sendMessage("Playback as been resumed.");
			} else {
				if (player.getAudioQueue().isEmpty())
					msg.getChannel().sendMessage("The current audio queue is empty! Add something to the queue first!");
				else {
					player.play();
					msg.getChannel().sendMessage("Player has started playing!");
				}
			}
		} else if (args[1].equals("play") && args.length > 2) {
			String infoMsg = "";
			String url = args[2];
			if(!inChannel){
				handleJoinChannel();
			}
			Playlist playlist = Playlist.getPlaylist(url, event.getMessage().getGuild().getID());
			List<AudioSource> sources = new LinkedList<AudioSource>(playlist.getSources());
			if (sources.size() > 1) {
				msg.getChannel().sendMessage("Found a playlist with **" + sources.size() + "** entries.\n"
						+ "Proceeding to gather information and queue sources. This may take some time...");
				final MusicPlayer fPlayer = player;
				Thread thread = new Thread() {
					@Override
					public void run() {
						if (!inChannel) {
							for (Iterator<AudioSource> it = sources.iterator(); it.hasNext();) {
								AudioSource source = it.next();
								AudioInfo info = source.getInfo();
								List<AudioSource> queue = fPlayer.getAudioQueue();
								if (info.getError() == null) {
									queue.add(source);
									if (fPlayer.isStopped())
										fPlayer.play();
								} else {
									try {
										msg.getChannel().sendMessage(
												"Error detected, skipping source. Error:\n" + info.getError());
									} catch (MissingPermissionsException e) {
										e.printStackTrace();
									} catch (RateLimitException e) {
										e.printStackTrace();
									} catch (DiscordException e) {
										e.printStackTrace();
									}
									it.remove();
								}
							}
							try {
								msg.getChannel()
										.sendMessage("Finished queuing provided playlist. Successfully queued **"
												+ sources.size() + "** sources");
							} catch (MissingPermissionsException e) {
								e.printStackTrace();
							} catch (RateLimitException e) {
								e.printStackTrace();
							} catch (DiscordException e) {
								e.printStackTrace();
							}
						}
					}
				};
				thread.start();
			} else {
				AudioSource source = sources.get(0);
				AudioInfo info = source.getInfo();
				if (info.getError() == null) {
					player.getAudioQueue().add(source);
					infoMsg += "The provided URL has been added the to queue";
					if (player.isStopped()) {
						player.play();
						infoMsg += " and the player has started playing";
					}
					msg.getChannel().sendMessage(infoMsg + ".");
				} else {
					msg.getChannel().sendMessage(
							"There was an error while loading the provided URL.\n" + "Error: " + info.getError());
				}
			}
		}
	}

	/**
	 * Handles skipping a song
	 * 
	 * @throws MissingPermissionsException
	 * @throws RateLimitException
	 * @throws DiscordException
	 */
	private void handleSkip() throws MissingPermissionsException, RateLimitException, DiscordException {
		player.skipToNext();
		msg.getChannel().sendMessage("Skipped the current song.");
	}

	/**
	 * Handles leaving the currently joined Discord channel
	 */
	private void handleLeaveChannel() {
		IDiscordClient client = msg.getClient();
		IVoiceChannel chan = client.getConnectedVoiceChannels().stream().filter(c -> c.getGuild() == msg.getGuild())
				.findFirst().orElse(null);
		if (chan != null)
			chan.leave();
		inChannel = false;

	}

	/**
	 * Handles joining a Discord Audio channel. The channel that the caller is
	 * in is the one that will be joined.
	 */
	private void handleJoinChannel() {
		try {
			IVoiceChannel voicechannel = msg.getAuthor().getConnectedVoiceChannels().get(0);
			voicechannel.join();
			inChannel = true;
		} catch (ArrayIndexOutOfBoundsException e) {
			try {
				msg.getChannel().sendMessage(msg.getAuthor().getName() + ", you aren't in a channel!");
			} catch (RateLimitException | DiscordException | MissingPermissionsException e1) {

			}
		} catch (MissingPermissionsException e) {
			try {
				msg.getChannel().sendMessage(
						msg.getAuthor().getName() + ", I don't have permission to join that channel.  Sorry!");
			} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
			}
		}
	}

	/**
	 * Handles listing the currently playing list.
	 * 
	 * @throws MissingPermissionsException
	 * @throws RateLimitException
	 * @throws DiscordException
	 */
	private void handleNowPlayingListing() throws MissingPermissionsException, RateLimitException, DiscordException {
		if (player.isPlaying()) {
			AudioTimestamp currentTime = player.getCurrentTimestamp();
			AudioInfo info = player.getCurrentAudioSource().getInfo();
			if (info.getError() == null) {
				msg.getChannel().sendMessage("**Playing:** " + info.getTitle() + "\n" + "**Time:**    ["
						+ currentTime.getTimestamp() + " / " + info.getDuration().getTimestamp() + "]");
			} else {
				msg.getChannel().sendMessage(
						"**Playing:** Info Error. Known source: " + player.getCurrentAudioSource().getSource() + "\n"
								+ "**Time:**    [" + currentTime.getTimestamp() + " / (N/A)]");
			}
		} else {
			msg.getChannel().sendMessage("The player is not currently playing anything!");
		}
	}

	/**
	 * Lists the queued music list
	 * 
	 * @throws MissingPermissionsException
	 * @throws RateLimitException
	 * @throws DiscordException
	 */
	private void handleListMusicQueue() throws MissingPermissionsException, RateLimitException, DiscordException {
		List<AudioSource> queue = player.getAudioQueue();
		if (queue.isEmpty()) {
			msg.getChannel().sendMessage("The queue is currently empty!");
			return;
		}

		MessageBuilder builder = new MessageBuilder(msg.getClient());
		builder.appendContent("__Current Queue.  Entries: " + queue.size() + "__\n");
		for (int i = 0; i < queue.size() && i < 10; i++) {
			AudioInfo info = queue.get(i).getInfo();
			if (info == null)
				builder.appendContent("*Could not get info for this song.*");
			else {
				AudioTimestamp duration = info.getDuration();
				builder.appendContent("`[");
				if (duration == null)
					builder.appendContent("N/A");
				else
					builder.appendContent(duration.getTimestamp());
				builder.appendContent("]` " + info.getTitle() + "\n");
			}
		}

		boolean error = false;
		int totalSeconds = 0;
		for (AudioSource source : queue) {
			AudioInfo info = source.getInfo();
			if (info == null || info.getDuration() == null) {
				error = true;
				continue;
			}
			totalSeconds += info.getDuration().getTotalSeconds();
		}

		builder.appendContent("\nTotal Queue Time Length: " + AudioTimestamp.fromSeconds(totalSeconds).getTimestamp());
		if (error)
			builder.appendContent("`An error occured calculating total time. Might not be completely valid.");
		builder.withChannel(msg.getChannel()).build();
	}

	/**
	 * Handles a volume change request.
	 * 
	 * @param args
	 *            arguments from channel
	 * @throws MissingPermissionsException
	 * @throws RateLimitException
	 * @throws DiscordException
	 */
	private void handleVolumeChange(String[] args)
			throws MissingPermissionsException, RateLimitException, DiscordException {
		if (args.length < 3) {
			msg.getChannel().sendMessage("Missing volume level argument");
			return;
		}
		float volume = Float.parseFloat(args[2]);
		volume = volume/100;
		volume = Math.min(1F, Math.max(0F, volume));
		player.setVolume(volume);
		msg.getChannel().sendMessage("Volume was changed to: " + (volume*100));
	}
}