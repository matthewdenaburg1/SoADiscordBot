package com.soa.rs.discordbot.bot.events;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.soa.rs.discordbot.jaxb.CurrentUser;
import com.soa.rs.discordbot.util.SoaClientHelper;
import com.soa.rs.discordbot.util.SoaLogging;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class UserTrackingQuery extends AbstractSoaMsgRcvEvent {

	private String[] args;

	public UserTrackingQuery(MessageReceivedEvent event) {
		super(event);
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	@Override
	public void executeEvent() {
		if (args[1].equalsIgnoreCase("setKnownUser") || args[1].equalsIgnoreCase("setKnownName")) {
			addKnownNameToUser();
		} else if (args[1].equalsIgnoreCase("search")) {
			searchUsers();
		} else if (args[1].equalsIgnoreCase("sendfile")) {
			sendTrackingFileToUser();
		}
	}

	private void searchUsers() {
		String searchTerm = null;
		Map<CurrentUser, Long> resultSet = null;
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

		searchTerm = getEvent().getMessage().getContent().substring(13);
		SoaLogging.getLogger().info("Search term: " + searchTerm);
		resultSet = UserTrackingUpdater.getInstance().searchUsers(searchTerm, false);

		if (resultSet.size() == 0) {
			if (!getEvent().getMessage().getMentions().isEmpty())
				searchTerm = getMentionedUser();
			if (searchTerm != null) {
				resultSet = UserTrackingUpdater.getInstance().searchUsers(searchTerm, false);
			}
		}

		if (resultSet.size() > 0) {
			Iterator<Entry<CurrentUser, Long>> mapIter = resultSet.entrySet().iterator();
			while (mapIter.hasNext()) {
				Map.Entry<CurrentUser, Long> entry = mapIter.next();
				EmbedBuilder builder = new EmbedBuilder();

				CurrentUser user = entry.getKey();

				builder.withTitle(getEvent().getClient().getUserByID(user.getUserId())
						.getDisplayName(getEvent().getClient().getGuildByID(entry.getValue().longValue())));
				builder.withDesc(
						SoaClientHelper.getDiscordUserNameForUser(getEvent().getClient().getUserByID(user.getUserId()))
								+ " in  server: "
								+ getEvent().getClient().getGuildByID(entry.getValue().longValue()).getName());
				if (user.getKnownName() != null) {
					builder.appendField("Known Name", user.getKnownName(), false);
				}
				sb.setLength(0);
				Iterator<String> iter = user.getDisplayNames().getDisplayName().iterator();
				while (iter.hasNext()) {
					String name = iter.next();
					sb.append(name);
					if (iter.hasNext()) {
						sb.append(", ");
					}
				}
				builder.appendField("All display names", sb.toString(), false);

				builder.appendField("Joined server date", sdf.format(user.getJoined().toGregorianCalendar().getTime()),
						true);
				builder.appendField("Last seen date", sdf.format(user.getLastOnline().toGregorianCalendar().getTime()),
						true);

				// Send Embed

				SoaClientHelper.sendEmbedToChannel(getEvent().getChannel(), builder);

			}
		} else {
			SoaClientHelper.sendMsgToChannel(getEvent().getChannel(), "Your search returned 0 results.");
		}

	}

	private void sendTrackingFileToUser() {
		try {
			InputStream stream = UserTrackingUpdater.getInstance().writeInfoToStream();
			if (stream != null) {
				SoaClientHelper.sendMsgWithFileToUser(getEvent().getAuthor().getLongID(), getEvent().getClient(),
						"File containing all user tracked information attached!", stream, "userInfo.xml");
			}
		} catch (Exception e) {
			SoaLogging.getLogger().error("There was an error sending the data to the user.", e);
		}

	}

	private void addKnownNameToUser() {
		try {
			String search = null;
			String name = null;
			int i = 2;

			StringBuilder sb = new StringBuilder();

			while (i < args.length) {
				if (args[i].equalsIgnoreCase("-search")) {
					i++;
					while (args.length > i && !args[i].equalsIgnoreCase("-name")) {
						sb.append(args[i]);
						sb.append(" ");
						i++;
					}
					search = sb.toString().trim();
					sb.setLength(0);
				} else if (args[i].equalsIgnoreCase("-name")) {
					i++;
					while (args.length > i && !args[i].equalsIgnoreCase("-search")) {
						sb.append(args[i]);
						sb.append(" ");
						i++;
					}
					name = sb.toString().trim();
					sb.setLength(0);

				}
			}

			if (search == null || name == null) {
				SoaClientHelper.sendMsgToChannel(getEvent().getChannel(), "Arguments provided were invalid.");
			} else if (search.startsWith("<@") && search.endsWith(">")) {
				search = getMentionedUser();
				if (search == null) {
					SoaClientHelper.sendMsgToChannel(getEvent().getChannel(), "Arguments provided were invalid.");
				}
			}

			if (search != null) {
				boolean updated = UserTrackingUpdater.getInstance().addKnownNameToUser(search, name);
				if (updated) {
					SoaClientHelper.sendMsgToChannel(getEvent().getChannel(), "User's known name updated");
				} else {
					SoaClientHelper.sendMsgToChannel(getEvent().getChannel(), "Was not able to find user");
				}
			}
		} catch (Exception e) {
			SoaClientHelper.sendMsgToChannel(getEvent().getChannel(), "An error occurred: " + e.getMessage());
			SoaLogging.getLogger().error("Error occurred in KnownUser", e);
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private String getMentionedUser() {
		List<IUser> mentionedUsers = getEvent().getMessage().getMentions();
		String name = null;

		SoaLogging.getLogger()
				.info("Checking mentioned users:" + SoaClientHelper.getDiscordUserNameForUser(mentionedUsers.get(0)));
		if (!mentionedUsers.isEmpty()) {
			name = SoaClientHelper.getDiscordUserNameForUser(mentionedUsers.get(0));
		}
		return name;
	}

}
