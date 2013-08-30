import web
import os

urls = (
    '/switchDeskLeft', 'switchDeskLeft',
    '/switchDeskRight', 'switchDeskRight',
    '/nextSlide', 'nextSlide',
    '/changeVolume', 'changeVolume'
)
app = web.application(urls, globals())

class switchDeskLeft:
  def GET(self):
    os.system("osascript -e 'tell application \"System Events\"' -e 'key down {control}' -e 'keystroke (key code 123)' -e 'key up {control}' -e 'end tell'")
    return "SUCCESS"

class switchDeskRight:
  def GET(self):
    os.system("osascript -e 'tell application \"System Events\"' -e 'key down {control}' -e 'keystroke (key code 124)' -e 'key up {control}' -e 'end tell'")
    return "SUCCESS"

class nextSlide:
  def GET(self):
    os.system("osascript -e 'tell application \"System Events\" to keystroke (key code 124)'")
    return "SUCCESS"

class changeVolume:

  def GET(self):
    volume = web.input(volume=None).volume
    if volume is None:
      return "Must provide volume parameter, ie '/changeVolume?volume=3.2'"
    os.system("osascript -e 'set volume %s'" % volume)

    return "SUCCESS"

if __name__ == "__main__":
    app.run()
