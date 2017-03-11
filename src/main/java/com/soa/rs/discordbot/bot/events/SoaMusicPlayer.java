package com.soa.rs.discordbot.bot.events;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.soa.rs.discordbot.util.GuildMusicManager;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * The SoaMusicPlayer is a slightly modified version of the implementation
 * example of the lavaplayer library which can be found on Github:
 * https://github.com/sedmelluq/lavaplayer
 * <p>
 * NOTE: The commands in this class are only permitted to be run by:
 * <ul>
 * <li>Eldars</li>
 * <li>Lians</li>
 * <li>Anyone temporarily assigned the role of "DJ"</li>
 * </ul>
 */
public class SoaMusicPlayer {

	private IMessage msg;
	private static final Logger logger = LogManager.getLogger();

	private final AudioPlayerManager playerManager;
	private final Map<Long, GuildMusicManager> musicManagers;

	public SoaMusicPlayer(IMessage msg) {
		this.playerManager = new DefaultAudioPlayerManager();
		this.musicManagers = new HashMap<>();

		// For use of playing YT, Soundcloud, etc
		AudioSourceManagers.registerRemoteSources(playerManager);

		this.msg = msg;
	}

	/**
	 * Sets the message parameter for the music player event
	 * 
	 * @param msg
	 *            IMessage object for the received message
	 */
	public void setMsg(IMessage msg) {
		this.msg = msg;
	}

	/**
	 * Get the GuildMusicManager for the provided guild. If one does not exist,
	 * one will be created.
	 * 
	 * @param guild
	 *            The guild for which a music manager will be retrieved
	 * @return The music manager for the guild
	 */
	private synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
		long guildId = Long.parseLong(guild.getID());
		GuildMusicManager musicManager = musicManagers.get(guildId);

		if (musicManager == null) {
			musicManager = new GuildMusicManager(playerManager);
			musicManagers.put(guildId, musicManager);
		}

		guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

		return musicManager;
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
		if (guild == null) {
			return false;
		}
		List<IRole> roleListing = new LinkedList<IRole>(event.getMessage().getAuthor().getRolesForGuild(guild));
		Iterator<IRole> roleIterator = roleListing.iterator();

		while (roleIterator.hasNext()) {
			IRole role = roleIterator.next();
			if (role.getName().equalsIgnoreCase("Eldar") || role.getName().equalsIgnoreCase("Lian")
					|| role.getName().equalsIgnoreCase("Arquendi") || role.getName().equalsIgnoreCase("DJ"))
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

		StringBuilder sb = new StringBuilder();
		if (!checkMusicRoles(event) && !args[1].equalsIgnoreCase("playlist")
				&& !args[1].equalsIgnoreCase("nowplaying")) {
			sb.append(event.getMessage().getAuthor().getName());
			sb.append(" attempted to run a music command but did not have the appropriate rank.");
			logger.info(sb.toString());
			msg.getChannel().sendMessage("Sorry, only Arquendi+ or DJ rank can run the music player");
			return;
		}

		if (args.length <= 1) {
			handleHelp();
			return;
		}

		sb.append(event.getMessage().getAuthor().getName());
		sb.append(" executed music command: ");
		sb.append(args[1]);
		String message = args[1];

		if (message.equals("join")) {
			logger.info(sb.toString());
			handleJoinChannel();
		}

		if (message.equals("leave")) {
			logger.info(sb.toString());
			handleLeaveChannel();
		}

		if (message.equals("help")) {
			logger.info(sb.toString());
			handleHelp();
		}

		if (message.equals("play")) {
			sb.append(" " + args[2]);
			logger.info(sb.toString());
			handlePlay(args, event.getMessage().getChannel());
		}

		if (message.equals("playlist")) {
			logger.info(sb.toString());
			handleListQueue(event.getMessage().getChannel());
		}

		if (message.equals("nowplaying")) {
			logger.info(sb.toString());
			handleNowPlaying(event.getMessage().getChannel());
		}

		if (message.equals("stop")) {
			logger.info(sb.toString());
			handleStop(event.getMessage().getChannel());
		}

		if (message.equals("pause")) {
			logger.info(sb.toString());
			handlePause(event.getMessage().getChannel());
		}

		if (message.equals("resume")) {
			logger.info(sb.toString());
			handleResume(event.getMessage().getChannel());
		}

		if (message.equals("skip")) {
			logger.info(sb.toString());
			handleSkip(event.getMessage().getChannel());
		}

		if (message.equals("volume")) {
			if (args.length == 3) {
				sb.append(" " + args[2]);
			}
			logger.info(sb.toString());
			handleVolume(event.getMessage().getChannel(), args);
		}
	}

	/**
	 * Handle playing a music track
	 * 
	 * @param args
	 *            The arguments containing the track to play
	 * @param channel
	 *            The text channel the command was entered in
	 */
	private void handlePlay(String[] args, IChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

		playerManager.loadItemOrdered(musicManager, args[2], new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				sendMessageToChannel(channel, "Adding to queue " + track.getInfo().title);

				musicManager.scheduler.queue(track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				AudioTrack firstTrack = playlist.getSelectedTrack();

				if (firstTrack == null) {
					firstTrack = playlist.getTracks().get(0);
				}

				sendMessageToChannel(channel, "Adding to queue " + firstTrack.getInfo().title
						+ " (first track of playlist " + playlist.getName() + ")");

				musicManager.scheduler.queue(firstTrack);

				for (int i = 1; i < playlist.getTracks().size(); i++) {
					firstTrack = playlist.getTracks().get(i);

					musicManager.scheduler.queue(firstTrack);
					logger.info("Adding song from Playlist: " + firstTrack.getInfo().title);
				}
			}

			@Override
			public void noMatches() {
				sendMessageToChannel(channel, "Nothing found by " + args[2]);
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				sendMessageToChannel(channel, "Could not play: " + exception.getMessage());
			}
		});
	}

	/**
	 * List out all entries in the current music queue
	 * 
	 * @param channel
	 *            The channel the message will be entered into
	 */
	private void handleListQueue(IChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

		BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
		AudioTrack track;

		Iterator<AudioTrack> iter = queue.iterator();

		StringBuilder sb = new StringBuilder();
		sb.append("Currently within the Music Queue:\n\n");
		int i = 1;

		if (!iter.hasNext()) {
			sb.append("Queue is empty.");
		} else {
			while (iter.hasNext()) {
				track = iter.next();
				sb.append(i + ": " + track.getInfo().title + "\n");
				i++;

				if (sb.length() > 1800) {
					sendMessageToChannel(channel, sb.toString());
					sb = new StringBuilder();
				}
			}
		}
		sendMessageToChannel(channel, sb.toString());

	}

	private void handleNowPlaying(IChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		AudioTrack track = musicManager.scheduler.getCurrentTrack();
		if (track != null) {
			sendMessageToChannel(channel, "Now playing: " + track.getInfo().title);
		} else {
			sendMessageToChannel(channel, "Nothing is currently playing.");
		}
	}

	/**
	 * Send a message to the specified channel
	 * 
	 * @param channel
	 *            The channel the message will be entered into
	 * @param message
	 *            The message to be entered
	 */
	private void sendMessageToChannel(IChannel channel, String message) {
		try {
			channel.sendMessage(message);
		} catch (Exception e) {
			logger.warn("Failed to send message {} to {}", message, channel.getName(), e);
		}
	}

	/**
	 * Stop a music track from playing
	 * 
	 * @param channel
	 *            The channel the command was entered in
	 */
	private void handleStop(IChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.player.stopTrack();
		musicManager.scheduler.emptyQueue();
	}

	/**
	 * Pause a currently playing music track
	 * 
	 * @param channel
	 *            The channel the command was entered in
	 */
	private void handlePause(IChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		if (!musicManager.player.isPaused()) {
			musicManager.player.setPaused(true);
			sendMessageToChannel(channel, "Playback paused");
		}
	}

	/**
	 * Resume a currently paused music track
	 * 
	 * @param channel
	 *            The channel the command was entered in
	 */
	private void handleResume(IChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		if (musicManager.player.isPaused()) {
			musicManager.player.setPaused(false);
			sendMessageToChannel(channel, "Playback resumed");
		}
	}

	/**
	 * Change the volume of the player
	 * 
	 * @param channel
	 *            The text channel the command was entered in
	 * @param args
	 *            The arguments containing volume to set the music at
	 */
	private void handleVolume(IChannel channel, String[] args) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

		if (args.length == 3) {
			int volume = Integer.parseInt(args[2]);
			musicManager.player.setVolume(volume);
			sendMessageToChannel(channel, "Volume set to " + volume);
		} else if (args.length == 2) {
			sendMessageToChannel(channel, "Current volume is " + musicManager.player.getVolume());
		}

	}

	/**
	 * Skip a currently playing music track
	 * 
	 * @param channel
	 *            The text channel the command was entered in
	 */
	private void handleSkip(IChannel channel) {
		GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
		musicManager.scheduler.nextTrack();
		sendMessageToChannel(channel, "Track skipped");
	}

	/**
	 * Handles the help command
	 */
	private void handleHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("```Help: Music (command: .music [args])\n");
		sb.append(
				"Note - This menu and these commands will only work for users assigned the role \"Eldar\", \"Lian\", \"Arquendi\", or \"DJ\"\n\n");

		sb.append(".music join - Bot joins the voice channel you are in.\n");
		sb.append(".music play <url> - Bot queues up the URL provided.\n");
		sb.append(".music pause - Bot pauses playback.\n");
		sb.append(".music resume - Bot resumes playback.\n");
		sb.append(".music skip - Bot skips the currently playing song.\n");
		sb.append(".music stop - Bot stops playing and empties playlist.\n");
		sb.append(".music playlist - Bot lists currently queued playlist.\n");
		sb.append(".music nowplaying - Bot lists currently playing track.\n");
		sb.append(".music volume <0-100> - Sets volume to appropriate level.\n");
		sb.append(".music leave - Bot leaves the voice channel.\n");
		sb.append(".music help - Bot displays this menu.```");

		try {
			msg.getChannel().sendMessage(sb.toString());
		} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
			logger.error("Error displaying music help", e);
		}
	}

	/**
	 * Handles joining a Discord Audio channel. The channel that the caller is
	 * in is the one that will be joined.
	 */
	private void handleJoinChannel() {
		try {
			IVoiceChannel voicechannel = msg.getAuthor().getConnectedVoiceChannels().get(0);
			voicechannel.join();

			// Purposely set the volume to be low, so that no one's ears are
			// blasted out if a track is loud
			GuildMusicManager musicManager = getGuildAudioPlayer(msg.getGuild());
			musicManager.player.setVolume(5);
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
	 * Handles leaving the currently joined Discord channel
	 */
	private void handleLeaveChannel() {
		handleStop(msg.getChannel());
		IDiscordClient client = msg.getClient();
		IVoiceChannel chan = client.getConnectedVoiceChannels().stream().filter(c -> c.getGuild() == msg.getGuild())
				.findFirst().orElse(null);
		if (chan != null)
			chan.leave();

	}
}
