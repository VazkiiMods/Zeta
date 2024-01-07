package org.violetmoon.zeta.network.message;

import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;
import org.violetmoon.zeta.network.ZetaHandshakeMessage;

import java.util.BitSet;

public class C2SLoginFlag extends ZetaHandshakeMessage {

	public BitSet flags;
	public int expectedLength;
	public int expectedHash;

	public C2SLoginFlag() {
		flags = SyncedFlagHandler.compileFlagInfo();
		expectedLength = SyncedFlagHandler.expectedLength();
		expectedHash = SyncedFlagHandler.expectedHash();
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		if(expectedLength == SyncedFlagHandler.expectedLength() && expectedHash == SyncedFlagHandler.expectedHash())
			SyncedFlagHandler.receiveFlagInfoFromPlayer(context.getSender(), flags);
		return true;
	}

}
