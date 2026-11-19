# AndroidScreencast

GitHub Pages publishes the project site from `docs/` on the `master` branch
(Jekyll, theme `jekyll-theme-cayman`). `docs/assets/css/style.scss` imports
the theme and then the shared identity.

## Design identity

This site uses the shared identity from xSAVIKx/xSAVIKx.github.io/identity.
- Do not edit the vendored `ys-*.css` file. To update it, copy the new file from the hub.
- Keep this project's own brand colours, logo and illustrations.
- Use system fonts only. Do not add a web font or serif headings.
- New text colours must meet 4.5:1 contrast in light and dark.

The vendored file is `docs/_sass/ys-jekyll-cayman.scss`. It is a byte-for-byte
copy of `ys-jekyll-cayman.css`. It has the `.scss` extension so that Sass puts
its contents into `style.css`.
