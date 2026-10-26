package com.aethone.client.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aethone.client.R
import com.aethone.client.core.VpnStatus
import com.aethone.client.data.AppSettings
import com.aethone.client.data.ExtraMode
import com.aethone.client.data.Transport
import com.aethone.client.ui.components.AethoneMark
import com.aethone.client.ui.theme.Aethone

/** Short label shown on the home pill / notification. */
fun protocolLabel(transport: Transport, extra: ExtraMode): String = when (extra) {
    ExtraMode.OFF -> when (transport) {
        Transport.MASQUE -> "MASQUE"
        Transport.WIREGUARD -> "WIREGUARD"
        Transport.GOOL -> "GOOL"
        Transport.GOOL_CLASSIC -> "GOOL CLASSIC"
        Transport.MIM -> "MIM"
    }
    ExtraMode.TOR -> "WARP → TOR"
    ExtraMode.TOR_REVERSE -> "TOR → WARP"
    ExtraMode.TOR_ONLY -> "TOR ONLY"
    ExtraMode.PSIPHON -> "WARP → PSIPHON"
    ExtraMode.PSIPHON_REVERSE -> "PSIPHON → WARP"
    ExtraMode.PSIPHON_ONLY -> "PSIPHON ONLY"
}

private data class PickerEntry(
    val transport: Transport,
    val extra: ExtraMode,
    val titleRes: Int,
    val descRes: Int,
)

@Composable
fun ProtocolPickerContent(
    current: AppSettings,
    onPick: (Transport, ExtraMode) -> Unit,
    onProxyOnlyChange: (Boolean) -> Unit,
) {
    val extras = Aethone.extras
    val entries = listOf(
        PickerEntry(Transport.MASQUE, ExtraMode.OFF, R.string.protocol_masque, R.string.protocol_masque_desc),
        PickerEntry(Transport.WIREGUARD, ExtraMode.OFF, R.string.protocol_wireguard, R.string.protocol_wireguard_desc),
        PickerEntry(Transport.GOOL, ExtraMode.OFF, R.string.protocol_gool, R.string.protocol_gool_desc),
        PickerEntry(Transport.GOOL_CLASSIC, ExtraMode.OFF, R.string.protocol_gool_classic, R.string.protocol_gool_classic_desc),
        PickerEntry(Transport.MIM, ExtraMode.OFF, R.string.protocol_mim, R.string.protocol_mim_desc),
        PickerEntry(Transport.MASQUE, ExtraMode.TOR, R.string.protocol_tor, R.string.protocol_tor_desc),
        PickerEntry(Transport.MASQUE, ExtraMode.TOR_REVERSE, R.string.protocol_tor_reverse, R.string.protocol_tor_reverse_desc),
        PickerEntry(Transport.MASQUE, ExtraMode.TOR_ONLY, R.string.protocol_tor_only, R.string.protocol_tor_only_desc),
        PickerEntry(Transport.MASQUE, ExtraMode.PSIPHON, R.string.protocol_psiphon, R.string.protocol_psiphon_desc),
        PickerEntry(Transport.MASQUE, ExtraMode.PSIPHON_REVERSE, R.string.protocol_psiphon_reverse, R.string.protocol_psiphon_reverse_desc),
        PickerEntry(Transport.MASQUE, ExtraMode.PSIPHON_ONLY, R.string.protocol_psiphon_only, R.string.protocol_psiphon_only_desc),
    )

    Column(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp)
    ) {
        Text(
            stringResource(R.string.protocol_mode_title),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = extras.text,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(extras.surfaceVariant, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ModeChoice(
                text = stringResource(R.string.protocol_mode_vpn),
                selected = !current.proxyOnly,
                modifier = Modifier.weight(1f),
                onClick = { onProxyOnlyChange(false) },
            )
            ModeChoice(
                text = stringResource(R.string.protocol_mode_proxy),
                selected = current.proxyOnly,
                modifier = Modifier.weight(1f),
                onClick = { onProxyOnlyChange(true) },
            )
        }
        if (current.proxyOnly) {
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.protocol_mode_proxy_desc, current.activeProxyPort),
                fontSize = 12.sp,
                color = extras.textDim,
            )
        }

        Spacer(Modifier.height(18.dp))
        Text(
            stringResource(R.string.protocol_title),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = extras.text,
        )
        Spacer(Modifier.height(8.dp))
        entries.forEach { e ->
            val selected = current.extraMode == e.extra &&
                (e.extra != ExtraMode.OFF || current.transport == e.transport)
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onPick(e.transport, e.extra) }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AethoneMark(
                    status = if (selected) VpnStatus.CONNECTED else VpnStatus.DISCONNECTED,
                    color = if (selected) extras.connected else extras.textDim,
                    glowColor = extras.connected,
                    modifier = Modifier.width(18.dp).height(18.dp),
                    glow = false,
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(e.titleRes),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = extras.text,
                    )
                    Text(
                        stringResource(e.descRes),
                        fontSize = 11.sp,
                        color = extras.textDim,
                    )
                }
                RadioButton(selected = selected, onClick = { onPick(e.transport, e.extra) })
            }
        }
    }
}

@Composable
private fun ModeChoice(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val extras = Aethone.extras
    Box(
        modifier
            .fillMaxHeight()
            .background(
                if (selected) extras.surface else androidx.compose.ui.graphics.Color.Transparent,
                RoundedCornerShape(11.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (selected) extras.text else extras.textDim,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
