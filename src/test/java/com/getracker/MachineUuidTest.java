package com.getracker;

import com.getracker.cerebro.MachineUuid;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class MachineUuidTest
{
	@Test
	public void canGenerateMachineUuid()
	{
		MachineUuid machineUuid = new MachineUuid();
		assertNotNull(machineUuid.getMachineUuid());
	}
}
