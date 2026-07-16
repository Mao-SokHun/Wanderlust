"""Generate branded Wanderlust APK QR posters.

Both posters open the branded download page (not raw GitHub),
so phones see the nice install screen first, then tap Download.
"""
from pathlib import Path

import qrcode
from PIL import Image, ImageDraw, ImageFilter

ROOT = Path(__file__).resolve().parents[1]
OUT = Path(__file__).resolve().parent
LOGO_PATH = ROOT / "Wanderlust" / "app" / "src" / "main" / "res" / "drawable" / "logo.png"

# Branded page — no ?auto=1 so users see the write-up before downloading.
DOWNLOAD_PAGE_URL = "https://wanderlust-api-dm3y.onrender.com/download/"

CORAL = (255, 107, 53)
GOLD = (212, 175, 120)
INK = (18, 14, 12)
CREAM = (255, 252, 249)

W, H = 1080, 1560


def rounded_mask(size: tuple[int, int], radius: int) -> Image.Image:
    mask = Image.new("L", size, 0)
    draw = ImageDraw.Draw(mask)
    draw.rounded_rectangle((0, 0, size[0] - 1, size[1] - 1), radius=radius, fill=255)
    return mask


def make_poster(url: str) -> Image.Image:
    base = Image.new("RGB", (W, H), INK)
    px = base.load()
    for y in range(H):
        ty = y / (H - 1)
        for x in range(W):
            tx = x / (W - 1)
            r = int(28 + 70 * (1 - tx) * (0.55 + 0.45 * (1 - ty)) + 8 * ty)
            g = int(16 + 28 * (1 - tx) + 55 * tx * (0.4 + 0.6 * ty))
            b = int(14 + 18 * (1 - tx) + 48 * tx)
            px[x, y] = (min(255, r), min(255, g), min(255, b))

    glow = Image.new("RGB", (W, H), (0, 0, 0))
    gdraw = ImageDraw.Draw(glow)
    gdraw.ellipse((-200, -180, 520, 420), fill=(120, 48, 28))
    gdraw.ellipse((W - 480, H - 520, W + 160, H + 80), fill=(0, 72, 70))
    glow = glow.filter(ImageFilter.GaussianBlur(120))
    base = Image.blend(base, glow, 0.42)
    base = base.filter(ImageFilter.GaussianBlur(1.2))
    img = base.copy()
    draw = ImageDraw.Draw(img)

    logo_src = Image.open(LOGO_PATH).convert("RGBA")
    logo_size = 320
    logo = logo_src.resize((logo_size, logo_size), Image.Resampling.LANCZOS)

    shadow = Image.new("RGBA", (logo_size + 48, logo_size + 48), (0, 0, 0, 0))
    sd = ImageDraw.Draw(shadow)
    sd.rounded_rectangle((14, 18, logo_size + 34, logo_size + 38), radius=62, fill=(0, 0, 0, 100))
    shadow = shadow.filter(ImageFilter.GaussianBlur(16))

    logo_x = (W - logo_size) // 2
    logo_y = 72
    img.paste(shadow, (logo_x - 24, logo_y - 14), shadow)

    mask = rounded_mask((logo_size, logo_size), 60)
    logo_rounded = Image.new("RGBA", (logo_size, logo_size), (0, 0, 0, 0))
    logo_rounded.paste(logo, (0, 0))
    logo_rounded.putalpha(mask)

    ring = Image.new("RGBA", (logo_size + 14, logo_size + 14), (0, 0, 0, 0))
    rd = ImageDraw.Draw(ring)
    rd.rounded_rectangle(
        (0, 0, logo_size + 13, logo_size + 13),
        radius=64,
        outline=CORAL + (230,),
        width=3,
    )
    img.paste(ring, (logo_x - 7, logo_y - 7), ring)
    img.paste(logo_rounded, (logo_x, logo_y), logo_rounded)

    uline_w = 72
    uline_x = (W - uline_w) // 2
    uline_y = logo_y + logo_size + 48
    draw.rounded_rectangle(
        (uline_x, uline_y, uline_x + uline_w, uline_y + 3),
        radius=2,
        fill=GOLD,
    )

    qr_card_top = uline_y + 176

    qr = qrcode.QRCode(
        version=None,
        error_correction=qrcode.constants.ERROR_CORRECT_M,
        box_size=14,
        border=2,
    )
    qr.add_data(url)
    qr.make(fit=True)
    qr_img = qr.make_image(fill_color="black", back_color="white").convert("RGB")
    qr_side = 540
    qr_img = qr_img.resize((qr_side, qr_side), Image.Resampling.NEAREST)

    pad = 34
    card_side = qr_side + pad * 2
    card = Image.new("RGB", (card_side, card_side), CREAM)
    card.paste(qr_img, (pad, pad))
    card_rgba = card.convert("RGBA")
    card_rgba.putalpha(rounded_mask((card_side, card_side), 34))

    plate = Image.new("RGBA", (card_side + 16, card_side + 16), (0, 0, 0, 0))
    pd = ImageDraw.Draw(plate)
    pd.rounded_rectangle(
        (0, 0, card_side + 15, card_side + 15),
        radius=40,
        outline=CORAL + (255,),
        width=4,
    )

    cshadow = Image.new("RGBA", (card_side + 60, card_side + 60), (0, 0, 0, 0))
    csd = ImageDraw.Draw(cshadow)
    csd.rounded_rectangle(
        (18, 24, card_side + 42, card_side + 48),
        radius=38,
        fill=(0, 0, 0, 100),
    )
    cshadow = cshadow.filter(ImageFilter.GaussianBlur(18))

    card_x = (W - card_side) // 2
    card_y = qr_card_top
    img.paste(cshadow, (card_x - 30, card_y - 18), cshadow)
    img.paste(plate, (card_x - 8, card_y - 8), plate)
    img.paste(card_rgba, (card_x, card_y), card_rgba)

    content_bottom = card_y + card_side + 88
    if content_bottom < H - 20:
        img = img.crop((0, 0, W, content_bottom))
    return img


def main() -> None:
    page = make_poster(DOWNLOAD_PAGE_URL)

    out_main = OUT / "wanderlust-apk-qr.png"
    out_direct = OUT / "wanderlust-apk-direct-qr.png"
    out_download = OUT / "wanderlust-download-qr.png"

    page.save(out_main, "PNG", optimize=True)
    page.save(out_download, "PNG", optimize=True)
    page.save(out_direct, "PNG", optimize=True)

    print(f"page_url={DOWNLOAD_PAGE_URL}")
    print(f"saved {out_main} size={page.size}")
    print(f"saved {out_direct} size={page.size}")
    print(f"saved {out_download} size={page.size}")


if __name__ == "__main__":
    main()
